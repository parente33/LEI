"""Greedy Search Algorithm."""
from typing import List, Optional, Any
import heapq

from interfaces import IAlgorithm
from config import VEHICLE_SPECS, VehicleType
from algorithms.informed.heuristic import Heuristic


class Greedy(IAlgorithm):
    """Optimizes for proximity to goal based on Heuristic only."""

    def find_path(self, graph: Any, start_node: int, end_node: int, vehicle: Any = None, traffic_manager: Any = None) -> Optional[List[int]]:
        """Greedy Search: f(n) = h(n)"""
        if start_node == end_node:
            return [start_node]

        specs = VEHICLE_SPECS.get(vehicle.type)

        # Priority Queue: (h_score, node_id)
        open_set = []
        heapq.heappush(open_set, (0.0, start_node))

        came_from = {}
        visited = set()
        visited.add(start_node)

        while open_set:
            _, current = heapq.heappop(open_set)

            if current == end_node:
                return self.reconstruct_path(came_from, current)

            if current not in graph: continue

            for neighbor_id in graph.neighbors(current):
                if neighbor_id not in visited:
                    visited.add(neighbor_id)
                    came_from[neighbor_id] = current

                    # f(n) = h(n)
                    h = Heuristic.calculate(graph, neighbor_id, end_node, specs)

                    heapq.heappush(open_set, (h, neighbor_id))

        return None
