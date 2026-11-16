"""Dataset Loader."""
import json
import random

from state import State
from structs.Vehicle import Vehicle
from structs.FillingStation import FillingStation
from structs.TaxiStand import TaxiStand
from structs.enums import VehicleType, StationType, VehicleStatus
from graph.map import load_map_data_to_graph
from config import VEHICLE_SPECS

class Loader:
    """Handles loading the simulation state from files."""

    @staticmethod
    def load(dataset_path: str) -> State:
        """Load dataset and build the initial State."""
        print(f"Loading dataset from {dataset_path}...")

        with open(dataset_path, 'r') as f:
            data = json.load(f)

        geography = data['geography']
        graph, stations_data, taxistand_nodes = load_map_data_to_graph(geography)

        stations = []
        for i, s_data in enumerate(stations_data):
            stype = s_data['type']
            sid   = f"S{i+1}"

            charging_rate = 22.0 if stype in [StationType.ELECTRIC, StationType.MIXED] else 0.0
            refuel_time   = 5.0

            station = FillingStation(
                id=sid,
                node_id=s_data['node_id'],
                type=stype,
                charging_rate_kwh_per_hour=charging_rate,
                refuel_time_minutes=refuel_time
            )
            stations.append(station)

        taxi_stands = []
        for i, node_id in enumerate(taxistand_nodes):
            tid   = f"TS{i+1}"
            stand = TaxiStand(id=tid, node_id=node_id)
            taxi_stands.append(stand)

        vehicles = []
        vehicle_configs = data.get('vehicles', [])

        type_map = {
            "ebus": VehicleType.EBUS,
            "eco": VehicleType.ECO,
            "gasoline": VehicleType.GASOLINE,
            "diesel": VehicleType.DIESEL
        }

        for v_conf in vehicle_configs:
            v_type_str = v_conf['type']
            v_type = type_map.get(v_type_str, VehicleType.GASOLINE)

            specs = VEHICLE_SPECS[v_type]

            # Position: Spawn at random taxi stand
            if taxi_stands:
                start_node = random.choice(taxi_stands).node_id
            else:
                start_node = list(graph.nodes.keys())[0] if graph.nodes else 0

            # Create Vehicle
            vehicle = Vehicle(
                id=v_conf['id'],
                type=v_type,
                current_node=start_node,
                path=[],
                path_index=0,
                status=VehicleStatus.AVAILABLE,
                capacity=specs['capacity'],
                max_energy=specs['max_energy'],
                current_energy=specs['max_energy'],
                assigned_request_id=None
            )
            vehicles.append(vehicle)

        # Build State
        return State(
            graph=graph,
            vehicles=vehicles,
            stations=stations,
            taxi_stands=taxi_stands,
            requests=[],
            current_time=0.0
        )
