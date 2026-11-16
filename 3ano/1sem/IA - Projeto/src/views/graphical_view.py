import matplotlib
try:
    matplotlib.use('Qt5Agg')
except:
    pass

import matplotlib.pyplot as plt
import networkx as nx
import numpy as np

from state import State
from interfaces import IView
from structs.enums import VehicleStatus, StationType


plt.style.use('dark_background')
COLOR_BG       = '#1e1e1e'
COLOR_NODE     = '#999999'
COLOR_EDGE     = '#5b5b5b'
COLOR_JAM_EDGE = '#ff4400' # Orange-Red

COLOR_VEHICLE_FREE   = '#00ff9d' # Neon Green
COLOR_VEHICLE_BUSY   = '#ff0055' # Neon Red
COLOR_VEHICLE_PICKUP = '#00ccff' # Neon Blue

COLOR_STATION = '#ffcc00' # Gold
COLOR_STAND   = '#bd93f9' # Purple


class GraphicalView(IView):
    """Modern Matplotlib visualization of the simulation."""

    def __init__(self, state: State):
        self.state = state

        self.fig, self.ax = plt.subplots(figsize=(12, 10))
        self.fig.patch.set_facecolor(COLOR_BG)

        self.ax.set_facecolor(COLOR_BG)
        self.ax.set_xticks([])
        self.ax.set_yticks([])
        self.ax.spines['top'].set_visible(False)
        self.ax.spines['right'].set_visible(False)
        self.ax.spines['bottom'].set_visible(False)
        self.ax.spines['left'].set_visible(False)

        self.node_positions = {}
        self.draw_graph     = nx.Graph()
        self._init_graph_data()

        plt.ion()
        self.show()

    def _init_graph_data(self):
        """Initialize graph data structure and draw STATIC elements."""
        self.draw_graph = self.state.graph
        for n, data in self.state.graph.nodes(data=True):
            self.node_positions[n] = (data.get('x', 0), data.get('y', 0))

        # --- DRAW STATIC ELEMENTS HERE (Once) ---

        #nx.draw_networkx_nodes(
        #    self.draw_graph,
        #    pos=self.node_positions,
        #    ax=self.ax,
        #    node_color=COLOR_NODE,
        #    node_size=0.5,
        #    alpha=1.0,
        #)

        nx.draw_networkx_edges(
            self.draw_graph,
            pos=self.node_positions,
            ax=self.ax,
            edge_color=COLOR_EDGE,
            width=1.0,
            alpha=0.6,
            arrows=False
        )

        sx, sy = [], []
        for s in self.state.stations:
            if s.node_id in self.node_positions:
                x, y = self.node_positions[s.node_id]
                sx.append(x)
                sy.append(y)
        self.ax.scatter(sx, sy, c=COLOR_STATION, marker='s', s=40, label='Stations', alpha=0.8)

        tx, ty = [], []
        for t in self.state.taxi_stands:
            if t.node_id in self.node_positions:
                x, y = self.node_positions[t.node_id]
                tx.append(x)
                ty.append(y)
        self.ax.scatter(tx, ty, c=COLOR_STAND, marker='P', s=40, label='Stands', alpha=0.8)

        self.ax.legend(loc='upper right', facecolor=COLOR_BG, labelcolor='white')

        # Holds for Dynamic objects
        self.jam_collection  = None
        self.vehicle_scatter = None

        # Initial empty scatter for vehicles
        self.vehicle_scatter = self.ax.scatter([], [], s=80, edgecolors='white', linewidth=1.5, zorder=10)

    def update(self, state: State):
        """Only redraw dynamic elements."""

        # Update Traffic Jams (Redraw only jams)
        if self.jam_collection:
            self.jam_collection.remove()
            self.jam_collection = None

        jammed_edges = list(self.state.traffic_manager.congested_edges.keys())
        if jammed_edges:
             # Filter to only edges in draw_graph
             valid_jams = [e for e in jammed_edges if self.draw_graph.has_edge(e[0], e[1])]
             if valid_jams:
                self.jam_collection = nx.draw_networkx_edges(
                    self.draw_graph,
                    pos=self.node_positions,
                    edgelist=valid_jams,
                    ax=self.ax,
                    edge_color=COLOR_JAM_EDGE,
                    width=1.50,
                    alpha=0.75,
                    arrows=False
                )

        # Update Vehicles
        vx, vy, vc = [], [], []
        for v in state.vehicles:
            x, y = 0, 0
            if v.current_node in self.node_positions:
                x, y = self.node_positions[v.current_node]

            # Interpolation
            if v.has_path() and getattr(v, 'dist_traveled_on_edge', 0) > 0:
                next_node = v.path[v.path_index + 1]
                if next_node in self.node_positions:
                    target_x, target_y = self.node_positions[next_node]
                    dist = np.sqrt((target_x - x)**2 + (target_y - y)**2)
                    if dist > 0:
                        fraction = min(v.dist_traveled_on_edge / dist, 1.0)
                        x = x + (target_x - x) * fraction
                        y = y + (target_y - y) * fraction

            vx.append(x)
            vy.append(y)

            if v.status == VehicleStatus.AVAILABLE:
                vc.append(COLOR_VEHICLE_FREE)
            elif v.status == VehicleStatus.OCCUPIED:
                vc.append(COLOR_VEHICLE_BUSY)
            else:
                vc.append(COLOR_VEHICLE_PICKUP)

        # Update Scatter Data directly using offsets
        if vx:
            self.vehicle_scatter.set_offsets(np.c_[vx, vy])
            self.vehicle_scatter.set_facecolors(vc)
            self.vehicle_scatter.set_visible(True)
        else:
            self.vehicle_scatter.set_visible(False)

        plt.pause(0.001)

    def show(self):
        plt.show(block=False)
