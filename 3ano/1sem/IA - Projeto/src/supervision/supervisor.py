"""Supervisor Implementation."""
import math
from typing import Tuple, Any, Optional

from state import State
from interfaces import ISupervisor, IAlgorithm
from structs.enums import VehicleStatus, RequestStatus, VehicleType
from structs.Vehicle import Vehicle
from structs.Request import Request

class Supervisor(ISupervisor):
    """
    Dispatcher that assigns requests to vehicles.
    Strategy: Filter-then-Solve.
    """

    def __init__(self, k_candidates: int = 3):
        self.k_candidates = k_candidates

    def assign(self, state: State, algorithm: IAlgorithm):
        """Assign pending requests to vehicles."""

        # Get Pending Requests (High Priority First)
        pending_requests = [r for r in state.requests if r.status == RequestStatus.PENDING]
        pending_requests.sort(key=lambda r: r.priority, reverse=True)

        if not pending_requests:
            return

        # Get Available Vehicles
        available_vehicles = [v for v in state.vehicles if v.status == VehicleStatus.AVAILABLE]

        if not available_vehicles:
            return

        # Assignment Loop
        for req in pending_requests:
            if not available_vehicles:
                break

            best_vehicle = None
            best_path    = None

            min_path_len = float('inf')

            # --- FILTER (Euclidean) ---
            # Get coordinates for request origin
            req_origin_coords = self._get_node_coords(state.graph, req.origin_node)
            if not req_origin_coords:
                continue

            candidates_scores = []
            for v in available_vehicles:
                # Filter: Environment Preference
                if req.env_preference:
                    is_green = v.type in [VehicleType.EBUS, VehicleType.ECO]
                    if not is_green:
                        continue  # Skip non-green if preferred

                v_coords = self._get_node_coords(state.graph, v.current_node)
                if not v_coords:
                    continue

                dist = self._euclidean(req_origin_coords, v_coords)
                candidates_scores.append((dist, v))

            # Sort and take Top-K
            candidates_scores.sort(key=lambda x: x[0])
            top_k = candidates_scores[:self.k_candidates]

            # --- SOLVE (Algorithm) ---
            for _, vehicle in top_k:
                # Pass traffic_manager from state if available
                tm   = getattr(state, 'traffic_manager', None)
                path = algorithm.find_path(state.graph, vehicle.current_node, req.origin_node, vehicle=vehicle, traffic_manager=tm)

                if path:
                    cost = len(path)

                    if cost < min_path_len:
                        min_path_len = cost
                        best_vehicle = vehicle
                        best_path    = path

            # --- ASSIGN ---
            if best_vehicle:
                # Update Request
                req.assigned_vehicle_id = best_vehicle.id
                req.status = RequestStatus.ASSIGNED

                # Update Vehicle
                best_vehicle.assigned_request_id = req.id
                best_vehicle.status = VehicleStatus.EN_ROUTE_TO_PICKUP
                best_vehicle.path = best_path
                best_vehicle.path_index = 0

                # Remove from available pool for this tick
                available_vehicles.remove(best_vehicle)

    def _get_node_coords(self, graph: Any, node_id: int) -> Optional[Tuple[float, float]]:
        """Helper to get x,y from graph node."""
        try:
            if node_id in graph.nodes:
                n = graph.nodes[node_id]
                return (n.get('x'), n.get('y'))
        except AttributeError:
            pass
        return None

    def _euclidean(self, c1: Tuple[float, float], c2: Tuple[float, float]) -> float:
        return math.sqrt(
            (c1[0] - c2[0])**2 +
            (c1[1] - c2[1])**2
        )
