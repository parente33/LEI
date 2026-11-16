"""Main entry point for TaxiGreen Simulator."""
import sys
import os
import time

sys.path.append(os.path.join(os.getcwd(), 'src'))

from loader import Loader
from simulator import Simulator
from supervision.supervisor import Supervisor

from algorithms.uninformed.bfs import BFS
from algorithms.uninformed.dfs import DFS
from algorithms.informed.astar import AStar
from algorithms.informed.greedy import Greedy

from views.console_view import ConsoleView
from views.graphical_view import GraphicalView


TITLE = """TaxiGreen Simulator
───────────────────
"""

MENU = """
Select algorithm
────────────────
Uninformed
  1) BFS
  2) DFS

Informed
  3) A*
  4) Greedy
"""

ALGORITHMS = {
    "1": ("BFS", BFS),
    "2": ("DFS", DFS),
    "3": ("A*", AStar),
    "4": ("Greedy", Greedy),
    "":  ("BFS", BFS),
}


def main():
    print(TITLE)

    dataset = "res/dataset.json"
    try:
        state = Loader.load(dataset)
    except Exception as e:
        print(f"Could not load dataset: {e}")
        return

    print(MENU)
    choice = input("Choice [1]: ").strip()

    name, AlgoClass = ALGORITHMS.get(choice, ALGORITHMS[""])
    print(f"Using {name}...")
    algorithm = AlgoClass()

    # Brain & Engine
    supervisor = Supervisor(k_candidates=3)
    simulator  = Simulator(state, supervisor, algorithm)

    # Views
    console = ConsoleView(update_interval=120)
    try:
        gui = GraphicalView(state)
        simulator.views.append(gui)
    except Exception as e:
        print(f"Could not initialize GraphicalView: {e}")

    simulator.views.append(console)

    # Main Loop
    print("\nStarting Simulation Loop... (Press Ctrl+C to stop)\n")
    try:
        step = 0
        while True:
            simulator.tick()

            for view in simulator.views:
                view.update(state)

            step += 1
            time.sleep(0.05)

    except KeyboardInterrupt:
        pass

    print("Simulation Terminated.")

if __name__ == "__main__":
    main()
