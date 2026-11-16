"""Shared Heuristic Logic."""
import math
from typing import Any

from config import ALGORITHM_CONFIG


class Heuristic:
    """Calculates heuristic cost for A* and Greedy."""

    @staticmethod
    def calculate(graph: Any, node_a: int, node_b: int, specs: dict = None) -> float:
        """Estimate cost from node_a to node_b."""
        try:
            n_a = graph.nodes[node_a]
            n_b = graph.nodes[node_b]
            coords_a = (n_a.get('x', 0), n_a.get('y', 0))
            coords_b = (n_b.get('x', 0), n_b.get('y', 0))
        except KeyError:
            return float('inf')

        # Euclidean Distance
        dx = coords_a[0] - coords_b[0]
        dy = coords_a[1] - coords_b[1]
        dist_m  = math.sqrt(dx*dx + dy*dy)
        dist_km = dist_m / 1000.0

        # Default specs if not provided
        if specs is None:
            specs = {
                'cost_per_km': ALGORITHM_CONFIG['default_cost_per_km'],
                'emissions': ALGORITHM_CONFIG['default_emissions']
            }

        max_speed    = ALGORITHM_CONFIG['max_speed_reference']
        min_time_min = dist_km / max_speed * 60.0

        min_money = dist_km * specs['cost_per_km']
        min_co2   = dist_km * specs['emissions']

        h_val = (ALGORITHM_CONFIG['weight_time'] * min_time_min) + \
                (ALGORITHM_CONFIG['weight_cost'] * min_money)    + \
                (ALGORITHM_CONFIG['weight_co2']  * min_co2)

        return h_val
