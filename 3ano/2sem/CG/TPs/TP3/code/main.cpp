#ifdef __APPLE__
#include <GLUT/glut.h>
#else
#include <GL/glut.h>
#endif

#define _USE_MATH_DEFINES
#include <math.h>

float alpha = 0.5f;
float beta = 0.5f;
float radius = 5.0f;
GLenum mode = GL_LINE;

void changeSize(int w, int h)
{
	// Prevent a divide by zero, when window is too short
	// (you cant make a window with zero width).
	if(h == 0)
		h = 1;

	// compute window's aspect ratio
	float ratio = w * 1.0 / h;

	// Set the projection matrix as current
	glMatrixMode(GL_PROJECTION);
	// Load Identity Matrix
	glLoadIdentity();

	// Set the viewport to be the entire window
    glViewport(0, 0, w, h);

	// Set perspective
	gluPerspective(45.0f ,ratio, 1.0f ,1000.0f);

	// return to the model view matrix mode
	glMatrixMode(GL_MODELVIEW);
}

void draw_axis()
{
	glBegin(GL_LINES);

	// X (red)
	glColor3f(1.0f, 0.0f, 0.0f);
	glVertex3f(-100.0f, 0.0f, 0.0f);
	glVertex3f( 100.0f, 0.0f, 0.0f);

	// Y (green)
	glColor3f(0.0f, 1.0f, 0.0f);
	glVertex3f(0.0f, -100.0f, 0.0f);
	glVertex3f(0.0f, 100.0f, 0.0f);

	// Z (blue)
	glColor3f(0.0f, 0.0f, 1.0f);
	glVertex3f(0.0f, 0.0f, -100.0f);
	glVertex3f(0.0f, 0.0f, 100.0f);

	glEnd();
}


void drawCylinder(float radius, float height, int slices)
{
	// put code to draw cylinder in here
	float step = 2.0f * M_PI / slices;
	float half = height / 2.0f;

	glBegin(GL_TRIANGLES);

	for (int i = 0; i < slices; i++)
	{
		float alpha = i * step;
		float nextAlpha = (i + 1) * step;

		float x1 = radius * cos(alpha);
		float z1 = radius * sin(alpha);

		float x2 = radius * cos(nextAlpha);
		float z2 = radius * sin(nextAlpha);

		// Top
		glColor3f(0.0f, 0.0f, 1.0f);

		glVertex3f(0.0f, half, 0.0f);
		glVertex3f(x2, half, z2);
		glVertex3f(x1, half, z1);

		// Bottom
		glColor3f(1.0f, 0.0f, 0.0f);

		glVertex3f(0.0f, -half, 0.0f);
		glVertex3f(x1, -half, z1);
		glVertex3f(x2, -half, z2);

		// Sides
		glColor3f(1.0f, 1.0f, 1.0f);

		glVertex3f(x2, -half, z2);
		glVertex3f(x1, -half, z1);
		glVertex3f(x2, half, z2);

		glVertex3f(x2, half, z2);
		glVertex3f(x1, -half, z1);
		glVertex3f(x1, half, z1);
	}

	glEnd();
}


void renderScene(void)
{
	// clear buffers
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	// set the camera
	glLoadIdentity();

	float px = radius * cos(beta) * sin(alpha);
	float py = radius * sin(beta);
	float pz = radius * cos(beta) * cos(alpha);

	gluLookAt(px, py, pz,
		      0.0, 0.0, 0.0,
			  0.0f, 1.0f, 0.0f);

	draw_axis();

	drawCylinder(1,2.0f,10);

	// End of frame
	glutSwapBuffers();
}


void processKeys(unsigned char c, int xx, int yy)
{
	// put code to process regular keys in here
	switch (c)
	{
		case '+':
			radius -= 0.1;
			break;
		case '-':
			radius += 0.1;
			break;
		case '1':
			mode = GL_FILL;
			break;
		case '2':
			mode = GL_LINE;
			break;
		case '3':
			mode = GL_POINT;
			break;
		default:
			break;
	}

	glutPostRedisplay();
}


void processSpecialKeys(int key, int xx, int yy)
{
	// put code to process special keys in here
	switch (key)
	{
		case GLUT_KEY_LEFT:
			beta -= 0.1;
			break;
		case GLUT_KEY_RIGHT:
			beta += 0.1;
			break;
		case GLUT_KEY_DOWN:
			alpha -= 0.1;
			break;
		case GLUT_KEY_UP:
			alpha += 0.1;
			break;
		default:
			break;
	}

	glutPostRedisplay();
}


int main(int argc, char **argv)
{
	// init GLUT and the window
	glutInit(&argc, argv);
	glutInitDisplayMode(GLUT_DEPTH|GLUT_DOUBLE|GLUT_RGBA);
	glutInitWindowPosition(100,100);
	glutInitWindowSize(800,800);
	glutCreateWindow("CG@DI-UM - Aula3");

	// Required callback registry
	glutDisplayFunc(renderScene);
	glutReshapeFunc(changeSize);

	// Callback registration for keyboard processing
	glutKeyboardFunc(processKeys);
	glutSpecialFunc(processSpecialKeys);

	//  OpenGL settings
	glEnable(GL_DEPTH_TEST);
	glEnable(GL_CULL_FACE);

	// enter GLUT's main cycle
	glutMainLoop();

	return 1;
}