"""DFS Algorithm Implementation."""
from typing import List, Optional, Any
from interfaces import IAlgorithm


class DFS(IAlgorithm):
    """Depth-First Search for pathfinding."""

    def find_path(self, graph: Any, start_node: int, end_node: int, vehicle: Any = None, traffic_manager: Any = None) -> Optional[List[int]]:
        """Find a path (not necessarily shortest) using DFS."""
        return self._dfs_recursive(graph, start_node, end_node, set(), [start_node])

    def _dfs_recursive(self, graph: Any, current_node: int, target_node: int, visited: set, path: List[int]) -> Optional[List[int]]:
        if current_node == target_node:
            return path

        visited.add(current_node)

        for neighbor in graph.neighbors(current_node):
            if neighbor not in visited:
                result = self._dfs_recursive(graph, neighbor, target_node, visited, path + [neighbor])
                if result:
                    return result

        return None
