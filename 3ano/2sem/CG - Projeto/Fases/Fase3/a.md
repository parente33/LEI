### Enquadramento e objectivos da Fase 1

A Fase 1 do projecto tem como propósito estabelecer uma pipeline geométrica e de renderização, assente em três pilares: (i) uma representação da *mesh* adequada a rendering por triângulos, (ii) um método consistente de indexação e orientação (winding) para garantir faces exteriores coerentes, e (iii) um formato de ficheiro simples (.3d) que permita desacoplar a geração de primitivas do carregamento no *engine*.

O resultado esperado nesta fase é a capacidade de gerar primitivas fundamentais (*plane*, *box*, *sphere* and *cone*), serializá-las e desenhá-las no motor com conectividade correcta (sem “buracos” ou triângulos invertidos), com uma convenção de faces exteriores bem definida.

___

### Representação da *Mesh*: dados geométricos e topologia

#### Separação entre geometria e topologia

A Mesh é representada por dois vectores principais:

a) **Vértices**:
Contém os dados geométricos e (no futuro) de shading. Nesta fase, cada vértice guarda apenas a posição 3D (x, y, z). Conceptualmente, esta estrutura é extensível para incluir normais, coordenadas de textura (UV), etc.

b) **Índices**:
Contém a descrição topológica da malha: como os vértices se ligam para formar triângulos. Cada grupo consecutivo de três índices define um triângulo.

Esta separação é central, porque permite a reutilização de vértices, isto é, o mesmo vértice pode ser referenciado por vários triângulos.


#### Propriedades e invariantes úteis

Uma *mesh* é considerada "desenhável" quando:

* existe pelo menos um vértice, e
* existe pelo menos um índice, e
* o número de índices é múltiplo de 3 (cada triângulo consome exactamente 3 índices).

A contagem de triângulos é, portanto:
triângulos = index_count / 3.

Em termos de integridade, é desejável garantir:

* ∀i ∈ indices: i < vertex_count (nenhum índice pode apontar para fora do array de vértices).
* index_count % 3 == 0 (topologia válida para triângulos).

___

### Convenção de orientação (*winding*) e definição de "exterior"

#### *Winding* e face "frontal"

A orientação de um triângulo é determinada pela ordem dos seus três índices. Adoptando a convenção CCW (counter-clockwise) como "frente", um triângulo é frontal se, ao observá-lo do lado exterior pretendido, os seus vértices estiverem enumerados no sentido anti-horário.

#### Normal geométrica e produto vectorial

Dado um triângulo com pontos p0, p1, p2, definem-se dois vectores aresta: \
e1 = p1 − p0 \
e2 = p2 − p0 \
A normal geométrica é: n = e1 × e2.

A direcção de n depende da ordem dos pontos. Inverter dois vértices inverte a normal. Assim, controlar a ordem dos índices é controlar a direcção "para fora" da superfície.


#### Objectivo prático

As primitivas foram concebidas para que a orientação dos triângulos corresponda consistentemente ao "lado de fora" do sólido. Isto é crucial para:

* backface culling (evita desenhar faces interiores),
* previsibilidade ao aplicar iluminação (normais com direcção coerente),
* consistência entre primitivas.

___

### Abstração fundamental: *grid* e indexação regular

#### Motivação

Muitas superfícies podem ser parametrizadas por dois parâmetros (u, v) e amostradas de forma regular. Ao amostrar (u, v) em passos uniformes, obtém-se uma grelha de vértices. A triangulação de uma grelha é um problema topológico repetido em múltiplas primitivas; por isso, a solução é abstrair a "emissão de índices" para uma grelha genérica.


#### Layout: mapeamento 2D -> 1D

Considera-se uma grelha com:

* (vdiv + 1) linhas (direcção v),
* (udiv + 1) colunas (direcção u).

Assumindo armazenamento "row-major":

    índice(j, i) = base + j * stride + i,

onde:

* base é o índice do primeiro vértice da patch dentro do vector global,
* stride é o número de vértices por linha (tipicamente udiv+1),
* j é o índice da linha,
* i é o índice da coluna.


#### Triangulação de cada célula

Cada célula quadrilateral (quad) entre (j,i) e (j+1,i+1) é dividida em dois triângulos ao longo de uma diagonal fixa. Uma escolha consistente é:

* triângulo 1: (j,i) -> (j,i+1) -> (j+1,i+1)
* triângulo 2: (j,i) -> (j+1,i+1) -> (j+1,i)

Esta estratégia:

* garante conectividade completa (sem buracos),
* mantém orientação consistente em toda a superfície,
* é facilmente generalizável para qualquer patch amostrada em grelha.


#### Benefício directo no projecto

A mesma lógica de indexação é reutilizada:

* no plano (uma grelha única),
* em cada face da caixa (seis grelhas),
* na zona "intermédia" da esfera (bandas entre anéis),
* na lateral do cone (grelha em altura × ângulo).


### Patch linear: parametrização base para faces planas

#### Definição

Um patch linear é definida por:

    P(s, t) = origin + u*s + v*t, com s,t ∈ [0,1].

Geometricamente, isto define um paralelogramo:

* origin é um canto,
* u e v são vectores de aresta.


#### Amostragem e grelha

A amostragem uniforme:

    s = i/udiv, i=0..udiv
    t = j/vdiv, j=0..vdiv

produz uma grelha **(vdiv+1) × (udiv+1)** directamente compatível com a indexação descrita anteriormente.


#### Orientação e normal da patch

O lado "frontal" da patch é determinado por cross(u, v). Ao escolher u e v adequadamente, controla-se o sentido do exterior (por exemplo, numa face da caixa, garantir que a normal aponta para fora do volume).

___

### Primitivas: equações, estrutura e algoritmos

#### *Plane*

Objectivo: gerar um quadrado no plano XZ, com Y=0, centrado na origem, de lado L.

Definição geométrica:

* h = L/2
* x ∈ [-h, +h], z ∈ [-h, +h], y = 0

Abordagem: \
O plano é uma patch linear com:

* origin num canto (por exemplo, (-h, 0, +h)),
* u ao longo de +X com comprimento L,
* v ao longo de -Z com comprimento L.

A escolha de v em -Z garante cross(u, v) = +Y, isto é, a face frontal é a superior (vista de cima). A triangulação é obtida pela grelha subdividida em d×d células e pelo indexador genérico.


#### *Box*

Objectivo: cubo centrado na origem, de lado L, com 6 faces orientadas para o exterior.

Estrutura:
A caixa é composta por 6 patches lineares independentes:

* topo (y=+h), normal +Y,
* fundo (y=-h), normal -Y,
* frente (z=+h), normal +Z,
* trás (z=-h), normal -Z,
* direita (x=+h), normal +X,
* esquerda (x=-h), normal -X.

Abordagem:
Para cada face escolhem-se *origin*, u e v de forma a:

* cobrir o quadrado correspondente no plano da face,
* garantir cross(u, v) alinhado com a normal exterior dessa face.

Decisão de duplicação de vértices:

As faces da caixa são geradas como *meshes* independentes e, por isso, não partilham vértices nas arestas nem nos cantos. Esta opção é intencional para preservar "hard edges" vai facilitar no futuro com a adição de normais e uv.


#### *Sphere*

Objectivo: esfera de raio R, com orientação exterior consistente e tratamento de singularidades (polos).

Parametrização esférica (eixo Y):

    φ ∈ [0, π] (polar)
    θ ∈ [0, 2π] (azimutal)

Equações:

    y = R cos(φ)   ,   r = R sin(φ),
    x = r cos(θ)   ,   z = r sin(θ).

Estrutura da *mesh*:

* vértice do polo norte: (0, +R, 0),
* anéis intermédios para k = 1..(stacks-1) com φ_k = k*(π/stacks),
* vértice do polo sul: (0, -R, 0).

*Seam* em θ:
Em cada anel usa-se (slices+1) amostras de θ, duplicando θ=2π em relação a θ=0. Esta duplicação facilita:

* fecho topológico sem lógica especial,
* mapeamento UV com u=0 e u=1 na mesma posição.

Indexação:
Divide-se em três zonas:

1. Tampa superior: fan do polo norte para o primeiro anel.
2. Bandas intermédias: grelha entre anéis consecutivos (indexação genérica).
3. Tampa inferior: fan do último anel para o polo sul.


#### Cone

Objectivo: cone fechado, com base circular (raio R) em y=0 e apex em y=H, com exterior coerente.

Lateral (parametrização):

    θ ∈ [0, 2π],  t ∈ [0, 1],

    y(t) = tH,
    raio(t) = (1 − t)R,

    x = raio(t) cos(θ),  z = raio(t) sin(θ).

Estratégia de amostragem:

* slices controla a discretização em θ,
* stacks controla o número de amostras ao longo de t (altura).

Tratamento do *apex*:
Evita-se gerar um anel com raio exactamente zero (que degeneraria para um conjunto de vértices coincidentes). Em vez disso:

* gera-se uma grelha lateral até ao último anel "próximo" do topo (raio ainda > 0),
* adiciona-se um vértice *apex* único,
* liga-se o último anel ao *apex* com um *fan* (um triângulo por slice), garantindo orientação exterior.

Base:

* gera-se um anel em y=0 com raio R (slices+1, com seam duplicada),
* adiciona-se um centro no (0,0,0),
* triangula-se com um fan centro->anel.
  A orientação é escolhida para que a face exterior da base aponte para -Y (exterior inferior), coerente num cone “em pé”.

Duplicação do anel da base:
O anel da base é separado do anel inferior da lateral para permitir normais distintas (base: normal constante -Y; lateral: normal inclinada).

___


### Formato do ficheiro .3d: serialização da *mesh*

#### Objectivo do formato

O .3d é um formato binário minimalista cujo objectivo é transportar exactamente o que o motor precisa para renderização indexada:

* número de vértices,
* número de índices,
* bloco de vértices,
* bloco de índices.

Isto permite:

* pré-gerar primitivas e reutilizá-las,
* carregar rapidamente (leitura sequencial),
* manter o motor independente dos detalhes de geração.

#### Layout do ficheiro

O formato é:

1. vertex_count: inteiro u32
2. index_count: inteiro u32
3. vertices: array de Vertex com vertex_count elementos
4. indices: array de u32 com index_count elementos


#### Considerações técnicas e invariantes

Para que o formato seja robusto e evolutivo, é importante explicitar:

a) Endianness
Assume-se little-endian (ambiente comum).

b) Estrutura do Vertex
Nesta fase, Vertex contém apenas 3 floats (posição). A evolução para normal/UV altera o tamanho do Vertex.

c) Validação ao carregar
Recomenda-se validar:

* index_count % 3 == 0,
* max(indices) < vertex_count,
* coerência do tamanho do ficheiro face às contagens.

___

### Síntese e conclusão

A solução desenvolvida na Fase 1 assenta numa arquitectura coerente:

* A *Mesh* é uma lista de vértices e uma lista de índices, adequada a rendering indexado.

* A indexação por grelha (row-major + stride) fornece um mecanismo genérico e reutilizável para triangulação de patches amostradas.

* A convenção de winding (CCW do exterior) é mantida por construção, através da escolha cuidadosa de parametrizações e, no caso de patches lineares, pela orientação de u e v.

* As primitivas são geradas a partir de equações matemáticas claras (*patch* linear, coordenadas esféricas, cone paramétrico), com tratamento explícito de singularidades (polos da esfera e *apex* do cone) via triangle *fans*.

* O formato .3d serializa directamente (*vertex_count*, *index_count*, *vertices*, *indices*), garantindo simplicidade e performance de carregamento.