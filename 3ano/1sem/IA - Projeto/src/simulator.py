"""Main Simulator Logic."""
from state import State
from interfaces import ISupervisor, IAlgorithm
from structs.enums import VehicleStatus, RequestStatus, VehicleType
from simulation.generator import RequestGenerator
from simulation.traffic import TrafficManager
from simulation.metrics import Metrics
from config import VEHICLE_SPECS, SIM_CONFIG

class Simulator:
    """Core simulation loop."""

    def __init__(self, state: State, supervisor: ISupervisor, algorithm: IAlgorithm):
        self.state = state
        self.supervisor = supervisor
        self.algorithm = algorithm
        self.generator = RequestGenerator(generation_interval=40)
        self.traffic_manager = TrafficManager()
        self.state.traffic_manager = self.traffic_manager
        self.metrics = Metrics()
        self.metrics.reset()
        self.state.metrics = self.metrics
        self.step_size = SIM_CONFIG["step_size_minutes"]
        self.views = []

    def tick(self):
        """Advance simulation by one step."""
        self.state.current_time += self.step_size

        self.traffic_manager.update(self.state, self.step_size)
        self._check_vehicle_autonomy()

        # Generate Requests
        new_reqs = self.generator.tick(self.state)
        self.state.requests.extend(new_reqs)

        # Assign Requests
        self.supervisor.assign(self.state, self.algorithm)

        self._move_vehicles()

    def _move_vehicles(self):
        """Update positions of all moving vehicles."""
        for v in self.state.vehicles:
            if v.status == VehicleStatus.EN_ROUTE_TO_PICKUP or v.status == VehicleStatus.OCCUPIED:
                if v.has_path():
                    current_node_id = v.path[v.path_index]
                    next_node_id    = v.path[v.path_index + 1]

                    if self.state.graph.has_edge(current_node_id, next_node_id):
                        data_all = self.state.graph.get_edge_data(current_node_id, next_node_id)
                        data = data_all[0] if 0 in data_all else next(iter(data_all.values()))

                        edge_len       = float(data.get('length', 500.0))
                        edge_speed_kph = float(data.get('speed_kph', 50.0))

                    # Apply Traffic Factor
                    edge_factor = self.traffic_manager.get_edge_factor(current_node_id, next_node_id)

                    # --- DYNAMIC RE-ROUTING ---
                    # If edge is heavily congested (e.g. < 0.3 factor), try to re-route
                    if edge_factor < 0.3:
                        dest_node = None
                        if v.path: dest_node = v.path[-1]

                        if dest_node and dest_node != next_node_id:
                            new_path = self.algorithm.find_path(
                                self.state.graph,
                                current_node_id,
                                dest_node,
                                vehicle=v,
                                traffic_manager=self.traffic_manager
                            )

                            if new_path:
                                # Check if new path actually changes the immediate decision
                                # If new_path[1] is same as current expected next_node, then the jam IS the best way.
                                new_next = new_path[1] if len(new_path) > 1 else None

                                if new_next and new_next != next_node_id:
                                    print(f" -> Detour found! Re-routing to {new_next}")
                                    v.path = new_path
                                    v.path_index = 0
                                    v.dist_traveled_on_edge = 0
                                    continue
                                else:
                                    pass
                            else:
                                pass
                    # --------------------------

                    real_speed_kph = edge_speed_kph * edge_factor

                    # Distance to travel this tick (step_size is in minutes)
                    # km/h * (min / 60) = km -> *1000 = meters
                    dist_to_travel = (real_speed_kph * (self.step_size / 60.0)) * 1000.0

                    v.dist_traveled_on_edge += dist_to_travel

                    # --- CONSUMPTION & METRICS LOGIC ---
                    dist_km = dist_to_travel / 1000.0
                    specs   = VEHICLE_SPECS.get(v.type, VEHICLE_SPECS[VehicleType.GASOLINE]) # Fallback

                    # Consume Energy
                    consumed          = dist_km * specs["consumption"]
                    v.current_energy -= consumed
                    if v.current_energy < 0: v.current_energy = 0

                    # Update Metrics
                    is_occupied = (v.status == VehicleStatus.OCCUPIED)
                    self.metrics.add_movement(
                        distance_km=dist_km,
                        is_occupied=is_occupied,
                        emissions_g=specs["emissions"],
                        cost_eur=specs["cost_per_km"]
                    )
                    # -----------------------------------

                    # Check if reached next node
                    if v.dist_traveled_on_edge >= edge_len:
                        overflow_dist  = v.dist_traveled_on_edge - edge_len
                        v.path_index  += 1
                        v.current_node = v.path[v.path_index]
                        v.dist_traveled_on_edge = overflow_dist

                        # Fix overflow looping
                        if v.dist_traveled_on_edge > 0:
                            v.dist_traveled_on_edge = 0


                # Check Arrival
                if v.path and v.path_index >= len(v.path) - 1:
                    # Arrived at destination/pickup
                    if v.status == VehicleStatus.EN_ROUTE_TO_PICKUP:
                        v.status = VehicleStatus.OCCUPIED

                        # Find Request
                        req = next((r for r in self.state.requests if r.id == v.assigned_request_id), None)
                        if req:
                            req.status = RequestStatus.PICKED_UP
                            # Plan Path to Destination
                            # Use the algorithm instance to find path
                            dropoff_path = self.algorithm.find_path(self.state.graph, v.current_node, req.destination_node, vehicle=v, traffic_manager=self.traffic_manager)
                            if dropoff_path:
                                v.path       = dropoff_path
                                v.path_index = 0
                            else:
                                # Edge case: No path to destination
                                print(f"No path for Vehicle {v.id} to Dest {req.destination_node}")

                    elif v.status == VehicleStatus.OCCUPIED:
                        # Arrived at Dropoff
                        v.status = VehicleStatus.AVAILABLE
                        req = next((r for r in self.state.requests if r.id == v.assigned_request_id), None)
                        if req:
                            req.status = RequestStatus.COMPLETED
                            wait_time  = self.state.current_time - req.created_time

                            self.metrics.complete_request(wait_time)
                            print(f"Request {req.id} COMPLETED by {v.id} (Wait: {wait_time:.1f}m)")

                        v.assigned_request_id = None
                        v.path = []
                        v.path_index = 0
                        v.dist_traveled_on_edge = 0

                    elif v.status == VehicleStatus.EN_ROUTE_TO_STATION:
                        # Arrived at Station
                        v.status = VehicleStatus.CHARGING_REFUELING
                        specs    = VEHICLE_SPECS.get(v.type, VEHICLE_SPECS[VehicleType.GASOLINE])
                        v.remaining_refuel_time = specs['refuel_time']
                        print(f"Vehicle {v.id} arrived at Station. Refueling for {v.remaining_refuel_time}m...")
                        v.path = []

    def _check_vehicle_autonomy(self):
        """Check for low energy and manage refueling."""
        LOW_ENERGY_THRESHOLD = 0.20 # 20%

        for v in self.state.vehicles:
            # Lifecycle: CHARGING -> AVAILABLE
            if v.status == VehicleStatus.CHARGING_REFUELING:
                v.remaining_refuel_time -= self.step_size
                if v.remaining_refuel_time <= 0:
                    v.current_energy = v.max_energy
                    v.status = VehicleStatus.AVAILABLE
                    print(f"Vehicle {v.id} finished refueling. Now AVAILABLE.")
                continue

            # Lifecycle: AVAILABLE -> EN_ROUTE_TO_STATION
            # Only trigger if available (not carrying passenger)
            if v.status == VehicleStatus.AVAILABLE:
                energy_ratio = v.current_energy / v.max_energy
                if energy_ratio < LOW_ENERGY_THRESHOLD:
                    # Find nearest compatible station
                    station = self._find_nearest_station(v)
                    if station:
                        print(f"Vehicle {v.id} Low Energy ({energy_ratio*100:.1f}%). Routing to Station {station.id}")
                        path = self.algorithm.find_path(self.state.graph, v.current_node, station.node_id, vehicle=v, traffic_manager=self.traffic_manager)
                        if path:
                            v.status = VehicleStatus.EN_ROUTE_TO_STATION
                            v.path = path
                            v.path_index = 0
                            v.dist_traveled_on_edge = 0

    def _find_nearest_station(self, vehicle):
        """Find the closest compatible station using simple distance heuristic first."""
        # Optimization: Filter by compatibility first
        compatible = [s for s in self.state.stations if s.supports_vehicle(vehicle.type) and s.is_available()]
        if not compatible: return None

        v_coords = self._get_node_coords(vehicle.current_node)

        best_s   = None
        min_dist = float('inf')

        for s in compatible:
            s_coords = self._get_node_coords(s.node_id)
            if v_coords and s_coords:
                import math
                dist = math.sqrt((v_coords[0]-s_coords[0])**2 + (v_coords[1]-s_coords[1])**2)
                if dist < min_dist:
                    min_dist = dist
                    best_s = s

        return best_s

    def _get_node_coords(self, node_id: int):
        """Helper to get coordinates for interpolation."""
        if node_id in self.state.graph.nodes:
            node = self.state.graph.nodes[node_id]
            return (node.get('x',0), node.get('y',0))
        return None
