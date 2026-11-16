#include "Application.h"
#include <iostream>

using namespace engine;

int
main (int argc, char *argv[])
{
  if (argc <= 1)
    {
      std::cerr << "Usage: " << argv[0] << " <scene.xml>\n";
      return 1;
    }

  Application app;
  if (app.init (argv[1]) != 0)
    {
      /* std::cerr << "error: init failed\n"; */
      return 1;
    }

  return app.run ();
}