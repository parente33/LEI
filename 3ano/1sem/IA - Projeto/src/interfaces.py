from abc import ABC, abstractmethod
from typing import List, Any, Optional


class IView(ABC):
    """Interface for simulation views."""

    @abstractmethod
    def update(self, state: Any):
        """Update the view with the new state."""
        pass


class IAlgorithm(ABC):
    """Interface for pathfinding algorithms."""

    @abstractmethod
    def find_path(self, graph: Any, start_node: int, end_node: int, vehicle: Any = None, traffic_manager: Any = None) -> Optional[List[int]]:
        """Calculate the path from start to end node, considering traffic if provided."""
        pass

    def reconstruct_path(self, came_from: dict, current: int) -> List[int]:
        path = [current]
        while current in came_from:
            current = came_from[current]
            path.append(current)

        path.reverse()
        return path


class ISupervisor(ABC):
    """Interface for the assignment supervisor."""

    @abstractmethod
    def assign(self, state: Any, algorithm: IAlgorithm) -> List[Any]:
        """
        Determines assignments of vehicles to requests.
        Returns a list of Assignment objects or changes state directly/returns instructions.
        """
        pass
