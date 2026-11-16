"""BFS Algorithm Implementation."""
from collections import deque
from typing import List, Optional, Any
from interfaces import IAlgorithm


class BFS(IAlgorithm):
    """Breadth-First Search for pathfinding."""

    def find_path(self, graph: Any, start_node: int, end_node: int, vehicle: Any = None, traffic_manager: Any = None) -> Optional[List[int]]:
        """Find strictly shortest path (in terms of hops) from start to end."""
        if start_node == end_node:
            return [start_node]

        queue = deque([(start_node, [start_node])])
        visited = {start_node}

        while queue:
            current_node, path = queue.popleft()

            if current_node == end_node:
                return path

            for neighbor in graph.neighbors(current_node):
                if neighbor not in visited:
                    visited.add(neighbor)
                    queue.append((neighbor, path + [neighbor]))

        return None
