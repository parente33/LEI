"""
Traffic simulation for the TaxiGreen system.
Handles dynamic changes in edge speeds based on time of day.
"""

import random
from typing import Dict, Tuple
from state import State


class TrafficManager:
    """
    Manages traffic conditions.
    Simulates dynamic edge-level congestion.
    """

    def __init__(self):
        self.current_global_factor = 1.0
        # Stores jammed edges: (u, v) -> {'factor': float, 'ttl': float}
        self.congested_edges: Dict[Tuple[int, int], Dict] = {}

        self.jam_probability = 0.05           # Chance per tick to spawn a jam
        self.max_jams = 10                    # Max concurrent jams
        self.min_jam_duration = 10.0
        self.max_jam_duration = 60.0
        self.jam_severity_range = (0.1, 0.4)  # Speed factor (10% to 40% speed)

    def get_edge_factor(self, u: int, v: int) -> float:
        """Get speed factor for a specific edge."""
        key = (u, v)
        if key in self.congested_edges:
            return self.congested_edges[key]['factor'] * self.current_global_factor

        return self.current_global_factor

    def update(self, state: State, time_delta: float):
        """Update traffic state: spawn/expire jams."""

        # Update Global Factor (Rush Hours)
        self.current_global_factor = self._get_global_factor(state.current_time)

        # Expire old jams
        expired = []
        for edge, data in self.congested_edges.items():
            data['ttl'] -= time_delta
            if data['ttl'] <= 0:
                expired.append(edge)

        for edge in expired:
            del self.congested_edges[edge]

        # Spawn new jams (Probabilistic)
        if len(self.congested_edges) < self.max_jams:
            if random.random() < self.jam_probability:
                self._spawn_jam(state)

    def _spawn_jam(self, state: State):
        """
        Randomly select a 'significant' edge to jam.
        Strategy: Focus on Main Roads (Length > 200m) in the CENTER.
        """
        if not state.graph: return

        all_edges = list(state.graph.edges)
        if not all_edges: return

        # candidate pool: "Big Edges" in "Center"
        candidates = []

        for e in all_edges:
            u, v = e[0], e[1]

            # Check Self-Loop
            if u == v:
                continue

            # Check Zone first
            if state.graph.nodes[u].get('zone') != 'center':
                continue

            # Check Edge Length
            data_dict = state.graph.get_edge_data(u, v)
            if not data_dict: continue

            e_data = data_dict[0] if 0 in data_dict else next(iter(data_dict.values()))
            length = float(e_data.get('length', 0))

            if length > 200.0:
                candidates.append(e)

        # Fallback to all edges if strict filter fails
        if not candidates:
            candidates = all_edges

        # Selection
        edge = random.choice(candidates)
        u, v = edge[0], edge[1]

        factor   = random.uniform(*self.jam_severity_range)
        duration = random.uniform(self.min_jam_duration, self.max_jam_duration)

        self.congested_edges[(u, v)] = {'factor': factor, 'ttl': duration}

    def _get_global_factor(self, current_time_minutes: float) -> float:
        """Time-of-day traffic factor."""
        t = current_time_minutes % 1440

        if 480 <= t < 570: return 0.7       # Morning Rush (8-9:30)
        if 1020 <= t < 1140: return 0.7     # Evening Rush (17-19)
        if t > 1320 or t < 300: return 1.1  # Night (Free flow)

        return 1.0
