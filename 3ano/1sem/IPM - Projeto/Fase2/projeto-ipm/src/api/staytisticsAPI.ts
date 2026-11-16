import axios from 'axios'
import type {
    City, Listing, Booking, Continent, DownloadFile, FAQCategory,
    CityStats, ComparisonInsight, ContinentAverage,
    CityDisplayData, DashboardData, ComparisonData
} from '@/types'

// ============================ CONFIGURAÇÃO DA API ============================ //

const API = axios.create({
    baseURL: 'http://localhost:3000',
    responseType: 'json',
    headers: {
        'Content-Type': 'application/json'
    }
})

const cache = {
    continents: null as Continent[] | null,
    bookings: null as Booking[] | null,
    listings: new Map<string, Listing[]>(),
    downloadFiles: null as DownloadFile[] | null,
    faqCategories: null as FAQCategory[] | null
}

// ============================ FUNÇÕES BÁSICAS ============================ //

/**
 * Devolve todos os continentes
 */
export async function getContinents(): Promise<Continent[]> {
    if (cache.continents) return cache.continents

    try {
        const response = await API.get<Continent[]>('/continents')
        cache.continents = response.data
        return response.data
    } catch (error) {
        console.error('Erro ao buscar continentes:', error)
        return []
    }
}

/**
 * Encontra cidade pelo seu slug
 */
export async function getCityBySlug(slug: string): Promise<City | null> {
    const continents = await getContinents()

    for (const continent of continents) {
        const city = continent.cities.find(c => {
            return c.slug === slug
        })

        if (city) {
            return city
        }
    }

    return null
}

/**
 * Devolve o continente de uma cidade
 */
export async function getContinentByCitySlug(slug: string): Promise<Continent | null> {
    const continents = await getContinents()

    for (const continent of continents) {
        if (continent.cities.some(c => c.slug === slug)) {
            return continent
        }
    }

    return null
}

/**
 * Devolve listings de uma cidade específica
 */
export async function getCityListings(citySlug: string): Promise<Listing[]> {
    if (cache.listings.has(citySlug)) {
        return cache.listings.get(citySlug)!
    }

    try {
        const response = await API.get('/listings')

        // listings é um objeto com chaves ("amesterdao", "berlim", etc)
        const listings = response.data[citySlug] || []

        cache.listings.set(citySlug, listings)
        return listings
    } catch (error) {
        console.error(`Erro ao buscar listings de ${citySlug}:`, error)
        return []
    }
}

/**
 * Devolve todos os bookings
 */
export async function getBookings(): Promise<Booking[]> {
    if (cache.bookings) return cache.bookings

    try {
        const response = await API.get<Booking[]>('/bookings')
        cache.bookings = response.data
        return response.data
    } catch (error) {
        console.error('Erro ao buscar bookings:', error)
        return []
    }
}

/**
 * Devolve bookings filtrados pela cidade
 */
export async function getCityBookings(citySlug: string): Promise<Booking[]> {
    const [allBookings, cityListings] = await Promise.all([
        getBookings(),
        getCityListings(citySlug)
    ])

    if (cityListings.length === 0) {
        return []
    }

    // Set com os IDs dos listings da cidade
    const cityListingIDs = new Set(cityListings.map(l => l.id))

    // Filtração
    const filteredBookings = allBookings.filter(booking => cityListingIDs.has(booking.listing_id))

    return filteredBookings
}

/**
 * Devolve todos os listings
 */
export async function getAllListings(): Promise<Record<string, Listing[]>> {
    const continents = await getContinents()
    const allListings: Record<string, Listing[]> = {}

    try {
        // Buscar todos os listings
        const response = await API.get('/listings')
        const listingsData = response.data

        // Para cada cidade nos continentes, buscar os seus listings
        const citySlugs = continents.flatMap(c => c.cities.map(city => city.slug))

        citySlugs.forEach(slug => {
            allListings[slug] = listingsData[slug] || []
        })

        return allListings
    } catch (error) {
        console.error('Erro ao buscar todos os listings:', error)
        return {}
    }
}

/**
 * Devolve arquivos para download
 */
export async function getDownloadFiles(): Promise<DownloadFile[]> {
    if (cache.downloadFiles) return cache.downloadFiles

    try {
        const response = await API.get<DownloadFile[]>('/downloadFiles')
        cache.downloadFiles = response.data
        return response.data
    } catch (error) {
        console.error('Erro ao buscar ficheiros de download:', error)
        return []
    }
}

/**
 * Devolve FAQs
 */
export async function getFaqs(): Promise<FAQCategory[]> {
    if (cache.faqCategories) return cache.faqCategories

    try {
        const response = await API.get<FAQCategory[]>('/faqCategories')
        cache.faqCategories = response.data
        return response.data
    } catch (error) {
        console.error('Erro ao buscar FAQs:', error)
        return []
    }
}

// ============================ FUNÇÕES COMPOSTAS ============================ //

/**
 * Devolve dados completos de uma cidade (para o CityView)
 */
export async function getCityDisplayData(citySlug: string): Promise<CityDisplayData> {
    try {
        const [city, listings, bookings] = await Promise.all([
            getCityBySlug(citySlug),
            getCityListings(citySlug),
            getCityBookings(citySlug)
        ])

        if (!city) {
            throw new Error(`Cidade "${citySlug}" não encontrada`)
        }

        const stats = calculateCityStats(listings, bookings)

        return {
            city,
            listings,
            bookings,
            stats
        }
    } catch (error) {
        console.error(`Erro em getCityDisplayData para ${citySlug}:`, error)
        throw error
    }
}

/**
 * Devolve dados da dashboard (para a Dashboard)
 */
export async function getDashboardData(citySlug: string): Promise<DashboardData> {
    try {
        const [city, listings, bookings, continent, allListings] = await Promise.all([
            getCityBySlug(citySlug),
            getCityListings(citySlug),
            getCityBookings(citySlug),
            getContinentByCitySlug(citySlug),
            getAllListings()
        ])

        if (!city) {
            console.error(`Cidade "${citySlug}" não encontrada`)
        }

        const continentAverages = calculateContinentAverages([continent!], allListings)
        const continentAvgPrice = continentAverages[continent?.name || ''] || 0

        const dashboardStats = {
            ...calculateCityStats(listings, bookings),
            ...calculateDashboardExtraStats(listings, bookings),
            roomTypes: [...new Set(listings.map(l => l.room_type))],
            mainRoomType: getMainRoomType(listings),
            currentContinent: continent?.name || 'Desconhecido',
            listingQualityScore: calculateQualityScore(listings, bookings),
            continentAvgPrice,
            priceDifference: calculatePriceDifference(
                calculateCityStats(listings, bookings).avgPrice,
                continentAvgPrice
            ),
            cancellationRate: calculateCancellationRate(bookings),
            estimatedRevenue: calculateEstimatedRevenue(listings, bookings),
            avgRevenuePerBooking: calculateAvgRevenuePerBooking(listings, bookings),
            avgGuestsPerBooking: calculateAvgGuestsPerBooking(bookings),
            peakMonths: getPeakMonths(bookings),
            topHosts: getTopHosts(listings),
            mostPopularType: getMostPopularType(listings)
        }

        return {
            city,
            listings,
            bookings,
            continents: continent ? [continent] : [],
            dashboardStats
        }

    } catch (error) {
        console.error(`Erro em getDashboardData(${citySlug}):`, error)
        throw error
    }
}

/**
 * Devolve dados para comparar (para a Comparisons)
 */
export async function getComparisonData(citySlug: string): Promise<{
    cityStats: CityStats
    cityExtraStats: any
    continentAverages: any
    comparisons: ComparisonInsight[]
}> {
    const [city, listings, bookings, continent] = await Promise.all([
        getCityBySlug(citySlug),
        getCityListings(citySlug),
        getCityBookings(citySlug),
        getContinentByCitySlug(citySlug)
    ])

    if (!city || !continent) {
        throw new Error(`Cidade ou continente não encontrados`)
    }

    // Obter estatísticas da cidade atual
    const cityStats = calculateCityStats(listings, bookings)
    const cityExtraStats = calculateDashboardExtraStats(listings, bookings)

    // Obter estatísticas continentais
    const continentStats = await getContinentStats(continent.name)

    // Remover a cidade atual das estatísticas continentais para calcular médias
    const otherCities = Object.entries(continentStats.cityStats)
      .filter(([slug]) => slug !== citySlug)

    const otherCityCount = otherCities.length

    let continentAvgPrice = 0

    if (otherCityCount > 0) {
      otherCities.forEach(([_, stats]) => {
        continentAvgPrice += stats.avgPrice
      })
      continentAvgPrice = Math.round(continentAvgPrice / otherCityCount)
    }

    // Calcular diferenças percentuais
    const priceDifference = calculatePriceDifference(cityStats.avgPrice, continentAvgPrice)
    const revenueDifference = calculatePercentageDifference(
      cityExtraStats.estimatedRevenue,
      continentStats.continentAverages.totalRevenue / (otherCityCount + 1)
    )
    const guestsDifference = calculatePercentageDifference(
      cityStats.totalGuests || 0,
      continentStats.continentAverages.totalGuests / (otherCityCount + 1)
    )

    // Gerar comparações
    const comparisons = await generateContinentComparisons(
      city,
      cityStats,
      cityExtraStats,
      continent,
      continentStats,
      priceDifference,
      revenueDifference,
      guestsDifference
    )

    return {
      cityStats,
      cityExtraStats,
      continentAverages: continentStats.continentAverages,
      comparisons
    }
}

// ============================ FUNÇÕES AUXILIARES ============================ //

/**
 * Calcula estatísticas básicas de uma cidade
 */
function calculateCityStats(listings: Listing[], bookings: Booking[]): CityStats {
    const totalListings = listings.length
    const avgPrice =  totalListings > 0
        ? Math.round(listings.reduce((sum, l) => sum + l.price, 0) / totalListings)
        : 0

    const executedBookings = bookings.filter(b => b.state === 'EXECUTED')

    const totalBookedNights = executedBookings.reduce((total, booking) => {
        const checkIn = new Date(booking.check_in)
        const checkOut = new Date(booking.check_out)
        const nights = Math.ceil((checkOut.getTime() - checkIn.getTime()) / (1000 * 60 * 60 * 24))
        return total + nights
    }, 0)

    const totalGuests = executedBookings.reduce((total, booking) => total + booking.nr_guests, 0)

    const avgStayDuration = executedBookings.length > 0
        ? Math.round(totalBookedNights / executedBookings.length)
        : 0

    const listingsWithBookings = listings.filter(listing =>
        executedBookings.some(b => b.listing_id === listing.id)
    ).length

    // Considerar que cada listing esté disponível 365 dias por ano (simplificação)
    // Dias disponíveis totais = número de listings com bookings × 365
    const totalAvailableDays = listingsWithBookings * 365

    // Taxa de ocupação = dias ocupados / dias disponíveis
    const occupancyRate = totalAvailableDays > 0
        ? Math.round((totalBookedNights / totalAvailableDays) * 100)
        : 0

    return {
        totalListings,
        avgPrice,
        totalBookings: bookings.length,
        executedBookings: executedBookings.length,
        totalBookedNights,
        totalGuests,
        avgStayDuration,
        occupancyRate: Math.min(occupancyRate, 100)
    }
}

/**
 * Calcula estatísticas para dashboard
 */
function calculateDashboardExtraStats(listings: Listing[], bookings: Booking[]) {
    const executedBookings = bookings.filter(b => b.state === 'EXECUTED')

    // Receita estimada
    const estimatedRevenue = executedBookings.reduce((total, booking) => {
        const checkIn = new Date(booking.check_in)
        const checkOut = new Date(booking.check_out)
        const nights = Math.ceil((checkOut.getTime() - checkIn.getTime()) / (1000 * 60 * 60 * 24))
        const listing = listings.find(l => l.id === booking.listing_id)
        return total + (listing ? listing.price * nights : 0)
    }, 0)

    // Receita média por booking
    const avgRevenuePerBooking = executedBookings.length > 0
        ? Math.round(estimatedRevenue / executedBookings.length)
        : 0

    // Nr hóspedes médio por booking
    const avgGuestsPerBooking = executedBookings.length > 0
        ? Math.round(
            executedBookings.reduce((sum, b) => sum + b.nr_guests, 0) / executedBookings.length
        )
        : 0

    // Taxa de cancelamento
    const canceledBookings = bookings.filter(b => b.state === 'CANCELLED').length
    const cancellationRate = bookings.length > 0
        ? Math.round((canceledBookings / bookings.length) * 100)
        : 0

    return {
        estimatedRevenue,
        avgRevenuePerBooking,
        avgGuestsPerBooking,
        cancellationRate
    }
}

/**
 * Calcula score de qualidade dos listings (simplificado)
 */
function calculateQualityScore(listings: Listing[], bookings: Booking[]): number {
    if (listings.length === 0) return 0

    let totalScore = 0

    listings.forEach(listing => {
        let score = 0

        if (listing.license && listing.license.trim() !== ''&& listing.license !== 'Pending') score += 3
        if (listing.last_review) {
            const lastReviewDate = new Date(listing.last_review)
            const now = new Date()
            const monthsDiff = (now.getFullYear() - lastReviewDate.getFullYear()) * 12 + (now.getMonth() - lastReviewDate.getMonth())
            if (monthsDiff <= 6) score += 2 // 2 pontos por ter reviews nos últimos 6 meses
        }
        // Preços moderados
        if (listing.price < 200) score += 2
        if (listing.price >= 200 && listing.price < 300) score += 1

        // Verifica se tem bookings
        const hasBookings = bookings.some(b => b.listing_id === listing.id)
        if (hasBookings) score += 2

        totalScore += score
    })

    // Normaliza para 0-100 (máximo possível: 9 pontos por listing)
    const maxPossibleScore = listings.length * 9
    return Math.round((totalScore / maxPossibleScore) * 100)
}

/**
 * Calcula médias de preço continentais
 */
function calculateContinentAverages(
    continents: Continent[],
    allListings: Record<string, Listing[]>
    ): ContinentAverage {

        const averages: ContinentAverage = {}

        continents.forEach(continent => {
            let totalPrice = 0
            let cityCount = 0

            continent.cities.forEach(city => {
                const cityListings = allListings[city.slug] || []
                if (cityListings.length > 0) {
                    const cityAvg = cityListings.reduce((sum, l) => sum + l.price, 0) / cityListings.length
                    totalPrice += cityAvg
                    cityCount += 1
                }
            })

            averages[continent.name] = cityCount > 0
                ? Math.round(totalPrice / cityCount)
                : 0
        })

        return averages
    }

/**
 * Função auxiliar para saber a diferença percentual entre dois preços
 */
function calculatePriceDifference(cityPrice: number, continentPrice: number): number {
    if (continentPrice === 0) return 0
    return Math.round(((cityPrice - continentPrice) / continentPrice) * 100)
}

/**
 * Calcular a percentagem de cancelamento nas reservas
 */
function calculateCancellationRate(bookings: Booking[]): number {
    const canceled = bookings.filter(b => b.state === 'CANCELLED').length
    return bookings.length > 0
                ? Math.round((canceled / bookings.length) * 100)
                : 0
}

/**
 * Calcula a receita estimada das reservas
 * Apenas se têm em conta as que já foram executadas
 */
function calculateEstimatedRevenue(listings: Listing[], bookings: Booking[]): number {
    const listingMap = new Map(listings.map(l => [l.id, l]))

    return bookings.reduce((total, booking) => {
        if (booking.state !== 'EXECUTED') return total

        const checkIn = new Date(booking.check_in)
        const checkOut = new Date(booking.check_out)
        const nights = (checkOut.getTime() - checkIn.getTime()) / (1000 * 60 * 60 * 24)

        if (nights <= 0) return total

        const listing = listingMap.get(booking.listing_id)
        return listing ? total + listing.price * Math.round(nights) : total
    }, 0)
}

/**
 * Calcula a receita média por booking feito
 */
function calculateAvgRevenuePerBooking(listings: Listing[], bookings: Booking[]): number {
    const executedBookings = bookings.filter(b => b.state === 'EXECUTED')
    if (executedBookings.length === 0) return 0

    const totalRevenue = calculateEstimatedRevenue(listings, bookings)
    return Math.round(totalRevenue / executedBookings.length)
}

/**
 * Calcula a média de hóspedes por booking
 */
function calculateAvgGuestsPerBooking(bookings: Booking[]): number {
    const executedBookings = bookings.filter(b => b.state === 'EXECUTED')
    if (executedBookings.length === 0) return 0

    const totalGuests = executedBookings.reduce((sum, b) => sum + b.nr_guests, 0)
    return Math.round(totalGuests / executedBookings.length)
}

const monthNames = ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez']

/**
 * Retorna os meses de maior procura
 */
function getPeakMonths(bookings: Booking[]): string {
    const monthCounts = bookings.reduce<Record<number, number>>((acc, b) => {
        if (b.state !== 'EXECUTED' && b.state !== 'RESERVED') return acc

        const date = new Date(b.check_in)
        if (isNaN(date.getTime())) return acc

        const month = date.getMonth()
        acc[month] = (acc[month] || 0) + 1
        return acc
    }, {})

    const peakMonths = Object.entries(monthCounts)
        .sort((a, b) => b[1] - a[1])
        .slice(0, 3)
        .map(([month]) => monthNames[Number(month)])

    return peakMonths.length ? peakMonths.join(', ') : 'N/A'
}

/**
 * Retorna os hosts com mais propriedades listadas
 */
function getTopHosts(listings: Listing[], limit: number = 3): Array<{ name: string; count: number}> {
    const hostCounts: Record<string, number> = {}

    listings.forEach(listing => {
        hostCounts[listing.host_name] = (hostCounts[listing.host_name] || 0) + 1
    })

    return Object.entries(hostCounts)
        .sort((a, b) => b[1] - a[1])
        .slice(0, limit)
        .map(([name, count]) => ({ name, count }))
}

/**
 * Retorna o tipo de quarto mais comum nas listagens (apenas o nome)
 */
function getMainRoomType(listings: Listing[]): string {
    const typeCounts: Record<string, number> = {}

    listings.forEach(listing => {
        typeCounts[listing.room_type] = (typeCounts[listing.room_type] || 0) + 1
    })

    const mainType = Object.entries(typeCounts)
                            .sort((a, b) => b[1] - a[1])[0]
    return mainType ? mainType[0] : 'N/A'
}

/**
 * Retorna o tipo de quarto mais comum nas listagens (junto com a percentagem relativa)
 */
function getMostPopularType(listings: Listing[]): { type: string; percentage: number } {
    const typeCounts: Record<string, number> = {}

    listings.forEach(listing => {
        typeCounts[listing.room_type] = (typeCounts[listing.room_type] || 0) + 1
    })

    const mainType = Object.entries(typeCounts)
                            .sort((a, b) => b[1] - a[1])[0]

    if (!mainType) {
        return { type: 'N/A', percentage: 0}
    }

    const percentage = Math.round((mainType[1] / listings.length) * 100)
    return { type: mainType[0], percentage }
}

/**
 * Calcula estatísticas para todas as cidades de um continente (modal de comparações)
 */
export async function getContinentStats(continentName: string): Promise<{
    cityStats: Record<string, CityStats>
    continentAverages: {
        avgPrice: number
        totalRevenue: number
        totalGuests: number
        popularRoomType: string
        popularRoomTypePercentage: number
    }
}> {
    const continents = await getContinents()
    const allListings = await getAllListings()
    const allBookings = await getBookings()

    const continent = continents.find(c => c.name === continentName)
    if (!continent) {
        throw new Error(`Continente "${continentName}" não encontrado`)
    }

    const cityStats: Record<string, CityStats> = {}
    let totalRevenue = 0
    let totalGuests = 0
    let totalPrice = 0
    let cityCount = 0

    const roomTypeCounts: Record<string, number> = {}

    for (const city of continent.cities) {
        const listings = allListings[city.slug] || []
        const cityBookings = allBookings.filter(booking =>
            listings.some(listing => listing.id === booking.listing_id)
        )

        const stats = calculateCityStats(listings, cityBookings)
        const extraStats = calculateDashboardExtraStats(listings, cityBookings)

        cityStats[city.slug] = stats

        totalRevenue += extraStats.estimatedRevenue
        totalGuests += stats.totalGuests || 0
        totalPrice += stats.avgPrice

        listings.forEach(listing => {
            const roomType = listing.room_type || 'Desconhecido'
            roomTypeCounts[roomType] = (roomTypeCounts[roomType] || 0) + 1
        })

        cityCount++
    }

    const popularRoomTypeEntry = Object.entries(roomTypeCounts)
        .sort((a, b) => b[1] - a[1])[0]

    const popularRoomType = popularRoomTypeEntry?.[0] || 'Unknown'
    const popularRoomTypeCount = popularRoomTypeEntry?.[1] || 0

    const totalRooms = Object.values(roomTypeCounts).reduce((a, b) => a + b, 0)
    const popularRoomTypePercentage = totalRooms > 0
        ? Math.round((popularRoomTypeCount / totalRooms) * 100)
        : 0

    return {
        cityStats,
        continentAverages: {
            avgPrice: cityCount > 0 ? Math.round(totalPrice / cityCount) : 0,
            totalRevenue,
            totalGuests,
            popularRoomType,
            popularRoomTypePercentage
        }
    }
}

/**
 * Calcula diferença percentual entre dois valores
 */
function calculatePercentageDifference(cityValue: number, continentValue: number): number {
    if (continentValue === 0) return 0
    return Math.round(((cityValue - continentValue) / continentValue) * 100)
}

/**
 * Gera frases de comparação
 */
async function generateContinentComparisons(
    city: City,
    cityStats: CityStats,
    cityExtraStats: any,
    continent: Continent,
    continentStats: any,
    priceDiff: number,
    revenueDiff: number,
    guestsDiff: number
): Promise<ComparisonInsight[]> {
    const comparisons: ComparisonInsight[] = []

    // 1. Comparação de preços
    const priceComparison = getPriceComparison(city.name, cityStats.avgPrice, priceDiff)
    comparisons.push({
      id: 1,
      title: 'Nível de Preços',
      description: priceComparison.description,
      value: `€${cityStats.avgPrice}`,
      trend: getTrend(priceDiff),
      comparison: priceComparison.tag
    })

    // 2. Comparação de receita
    const revenueComparison = getRevenueComparison(city.name, cityExtraStats.estimatedRevenue, revenueDiff)
    comparisons.push({
      id: 2,
      title: 'Receita Estimada',
      description: revenueComparison.description,
      value: formatCurrency(cityExtraStats.estimatedRevenue),
      trend: getTrend(revenueDiff),
      comparison: revenueComparison.tag
    })

    // 3. Comparação de hóspedes
    const guestsComparison = getGuestsComparison(city.name, cityStats.totalGuests || 0, guestsDiff)
    comparisons.push({
      id: 3,
      title: 'Número de Hóspedes',
      description: guestsComparison.description,
      value: cityStats.totalGuests?.toString() || '0',
      trend: getTrend(guestsDiff),
      comparison: guestsComparison.tag
    })

    // 4. Comparação de tipos de alojamento
    const roomTypeComparison = await getRoomTypeComparison(city, continentStats)
    comparisons.push({
      id: 4,
      title: 'Tipo de Alojamento',
      description: roomTypeComparison.description,
      value: getMainRoomType(await getCityListings(city.slug)),
      trend: roomTypeComparison.trend,
      comparison: roomTypeComparison.tag
    })

    return comparisons
}

/**
 * Determina se é higher, lower ou similar
 */
function getTrend(difference: number): 'higher' | 'lower' | 'similar' {
    if (difference > 10) return 'higher'
    if (difference < -10) return 'lower'
    return 'similar'
}

/**
 * Formata moeda
 */
const formatCurrency = (value: number) => {
    if (value >= 1000000) {
        return `€${(value / 1000000).toFixed(1)}M`
    } else if (value >= 1000) {
        return `€${(value / 1000).toFixed(1)}K`
    }
    return `€${value}`
}

/**
 * Gera frases para comparação de preços
 */
function getPriceComparison(cityName: string, cityPrice: number, difference: number): {
    description: string
    tag: string
} {
    const absDiff = Math.abs(difference)

    if (difference > 10) {
        return {
            description: `${cityName} tem preços/noite ${absDiff}% mais altos do que a média continental`,
            tag: `${absDiff}% acima da média`
      }
    } else if (difference < -10) {
        return {
            description: `${cityName} tem preços/noite ${absDiff}% mais baixos do que a média continental`,
            tag: `${absDiff}% abaixo da média`
      }
    } else {
        return {
            description: `Os preços em ${cityName} estão alinhados com a média continental`,
            tag: `Similar à média`
      }
    }
}

/**
 * Gera frases para comparação de receita
 */
function getRevenueComparison(cityName: string, cityRevenue: number, difference: number): {
    description: string
    tag: string
} {
    const absDiff = Math.abs(difference)

    if (difference > 10) {
        return {
            description: `Em média, as outras cidades do continente de ${cityName} faturam menos ${absDiff}% por ano`,
            tag: `${absDiff}% acima da média`
      }
    } else if (difference < -10) {
        return {
            description: `Em média, as outras cidades do continente de ${cityName} faturam mais ${absDiff}% por ano`,
            tag: `${absDiff}% abaixo da média`
      }
    } else {
        return {
            description: `A receita estimada em ${cityName} está alinhada com a média continental`,
            tag: `Similar à média`
      }
    }
}

/**
 * Gera frases para comparação de hóspedes
 */
function getGuestsComparison(cityName: string, cityGuests: number, difference: number): {
    description: string
    tag: string
} {
    const absDiff = Math.abs(difference)

    if (difference > 10) {
        return {
            description: `Até ao momento, foram registados significativamente mais hóspedes em ${cityName} do que no resto do continente`,
            tag: `${absDiff}% acima da média`
      }
    } else if (difference < -10) {
        return {
            description: `Até ao momento, foram registados menos hóspedes em ${cityName} comparado com o resto do continente`,
            tag: `${absDiff}% abaixo da média`
      }
    } else {
        return {
            description: `O número de hóspedes em ${cityName} está em linha com a média continental`,
            tag: `Similar à média`
      }
    }
}

/**
 * Gera frases para comparação de tipos de alojamento
 */
async function getRoomTypeComparison(city: City, continentStats: any): Promise<{
    description: string
    tag: string
    trend: 'higher' | 'lower' | 'similar' | 'info'
}> {
    const cityListings = await getCityListings(city.slug)
    const cityMainType = getMainRoomType(cityListings)
    const continentMainType = continentStats.continentAverages.popularRoomType

    if (cityMainType === continentMainType) {
        return {
            description: `Há tendências similares no tipo de alojamento listado no continente, prevalecendo o ${cityMainType}`,
            tag: `Igual ao continente`,
            trend: 'similar'
      }
    } else {
        return {
            description: `${city.name} tem preferência por ${cityMainType}, enquanto no continente predomina ${continentMainType}`,
            tag: `Diferente do continente`,
            trend: 'info'
      }
    }
}

// ============================ EXPORT ============================ //

export const staytisticsAPI = {
    getContinents,
    getCityBySlug,
    getCityListings,
    getBookings,
    getCityBookings,
    getAllListings,
    getDownloadFiles,
    getFaqs,

    getCityDisplayData,
    getDashboardData,
    getComparisonData,

    clearCache: () => {
        cache.continents = null
        cache.bookings = null
        cache.listings.clear()
        cache.downloadFiles = null
        cache.faqCategories = null
    }
}

export default staytisticsAPI