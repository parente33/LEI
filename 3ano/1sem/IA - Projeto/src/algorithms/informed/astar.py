"""A* Search Algorithm"""
from typing import List, Optional, Any
import heapq

from interfaces import IAlgorithm
from config import VEHICLE_SPECS, VehicleType, ALGORITHM_CONFIG
from algorithms.informed.heuristic import Heuristic


class AStar(IAlgorithm):
    """Optimizes for the cost function and heuristic."""

    def find_path(self, graph: Any, start_node: int, end_node: int, vehicle: Any = None, traffic_manager: Any = None) -> Optional[List[int]]:
        """A* Search: f(n) = g(n) + h(n)"""
        if start_node == end_node:
            return [start_node]

        # Get Vehicle Specs for Cost Function
        specs = None
        if vehicle and hasattr(vehicle, 'type'):
            specs = VEHICLE_SPECS.get(vehicle.type)

        # Priority Queue: (f_score, node_id)
        open_set = []
        heapq.heappush(open_set, (0.0, start_node))

        came_from = {}

        # g_score: Default infinity
        g_score = {node: float('inf') for node in graph.nodes}
        g_score[start_node] = 0.0

        # f_score: Default infinity
        f_score = {node: float('inf') for node in graph.nodes}
        f_score[start_node] = self._heuristic(graph, start_node, end_node, specs)

        nodes_expanded = 0

        while open_set:
            current_f, current = heapq.heappop(open_set)
            nodes_expanded += 1

            if current == end_node:
                return self.reconstruct_path(came_from, current)

            if current not in graph: continue

            for neighbor_id in graph.neighbors(current):
                edge_data_all = graph.get_edge_data(current, neighbor_id)
                edge_data     = edge_data_all[0] if 0 in edge_data_all else next(iter(edge_data_all.values()))

                # edge_data has {length (m), speed_kph, travel_time (s)}
                dist_m    = float(edge_data.get('length', 500.0))
                speed_kph = float(edge_data.get('speed_kph', 50.0))
                if speed_kph <= 0: speed_kph = 50.0

                # --- DYNAMIC TRAFFIC FACTOR ---
                factor = 1.0
                if traffic_manager:
                    factor = traffic_manager.get_edge_factor(current, neighbor_id)

                real_speed = speed_kph * factor
                if real_speed < 1.0: real_speed = 1.0 # Min speed cap

                time_min = (dist_m / 1000.0) / real_speed * 60.0
                dist_km  = dist_m / 1000.0

                # --- COST FUNCTION g(n) ---
                # Real cost of traversing this edge
                cost_time  = time_min
                cost_money = dist_km * specs['cost_per_km']
                cost_co2   = dist_km * specs['emissions']

                edge_cost = (ALGORITHM_CONFIG['weight_time'] * cost_time) + \
                            (ALGORITHM_CONFIG['weight_cost'] * cost_money) + \
                            (ALGORITHM_CONFIG['weight_co2']  * cost_co2)
                # --------------------------

                tentative_g = g_score[current] + edge_cost

                if tentative_g < g_score.get(neighbor_id, float('inf')):
                    # Found better path
                    came_from[neighbor_id] = current
                    g_score[neighbor_id] = tentative_g

                    h = self._heuristic(graph, neighbor_id, end_node, specs)
                    f = tentative_g + h
                    f_score[neighbor_id] = f

                    # Push to PQ
                    heapq.heappush(open_set, (f, neighbor_id))

        return None # No path

    def _heuristic(self, graph: Any, node_a: int, node_b: int, specs: dict) -> float:
        return Heuristic.calculate(graph, node_a, node_b, specs)
