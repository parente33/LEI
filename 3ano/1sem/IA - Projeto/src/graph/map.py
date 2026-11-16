import osmnx as ox
from typing import Tuple, List, Dict, Any

from structs.enums import StationType


def load_map_data_to_graph(geography: str) -> Tuple[Any, List[Dict[str, Any]], List[int]]:
    """Loads map data from OpenStreetMap using OSMnx."""
    print(f"Loading map data for: {geography}...")

    try:
        ox_graph = _load_and_process_graph(geography)
        stations_data, taxi_stands = _fetch_pois(geography, ox_graph)

        print(f"Graph created: {len(ox_graph.nodes)} nodes. Found {len(stations_data)} stations, {len(taxi_stands)} taxi stands.")
        return ox_graph, stations_data, taxi_stands

    except Exception as e:
        print(f"Could not load map: {e}")
        raise e

def _load_and_process_graph(geography: str):
    """Loads raw graph from OSM and adds speed/time attributes."""
    ox_graph = ox.graph_from_place(geography, network_type="drive")
    ox_graph = ox.add_edge_speeds(ox_graph)
    ox_graph = ox.add_edge_travel_times(ox_graph)
    ox_graph = ox.project_graph(ox_graph)

    # Remove Self-Loops
    self_loops = [(u, v, k) for u, v, k in ox_graph.edges(keys=True) if u == v]
    if self_loops:
        ox_graph.remove_edges_from(self_loops)
        print(f"Cleaned {len(self_loops)} self-loop edges.")

    _assign_zones(ox_graph)

    return ox_graph

def _assign_zones(graph):
    """Calculates map centroid and assigns 'center'/'outskirt' zones to nodes."""
    if not graph.nodes: return

    # Calculate Centroid
    xs = [data.get('x', 0) for _, data in graph.nodes(data=True)]
    ys = [data.get('y', 0) for _, data in graph.nodes(data=True)]

    avg_x = sum(xs) / len(xs)
    avg_y = sum(ys) / len(ys)

    # Calculate Max Radius
    max_sq_dist = 0
    import math
    for x, y in zip(xs, ys):
        d2 = (x - avg_x)**2 + (y - avg_y)**2
        if d2 > max_sq_dist:
            max_sq_dist = d2

    max_dist = math.sqrt(max_sq_dist)

    # Assign Zones
    # Threshold: Inner 30% is "Downtown"
    threshold = max_dist * 0.30

    for n, data in graph.nodes(data=True):
        x, y = data.get('x', 0), data.get('y', 0)
        dist = math.sqrt((x - avg_x)**2 + (y - avg_y)**2)

        if dist < threshold:
            graph.nodes[n]['zone'] = 'center'
        else:
            graph.nodes[n]['zone'] = 'outskirt'


def _fetch_pois(geography: str, ox_graph) -> Tuple[List[Dict], List[int]]:
    """Fetches Filling Stations and Taxi Stands from OSM."""
    stations_data = []
    taxi_stands   = []

    try:
        tags = {'amenity': ['fuel', 'charging_station', 'taxi']}
        pois = ox.features_from_place(geography, tags)

        if pois.empty:
            return [], []

        graph_crs = ox_graph.graph['crs']
        if pois.crs != graph_crs:
            pois = pois.to_crs(graph_crs)

        pois['centroid'] = pois.geometry.centroid

        def get_station_entry(nid):
            for s in stations_data:
                if s['node_id'] == nid:
                    return s
            return None

        for _, row in pois.iterrows():
            amenity = row.get('amenity')
            if not amenity:
                continue

            nearest_node = ox.nearest_nodes(ox_graph, row['centroid'].x, row['centroid'].y)

            if amenity == 'taxi':
                taxi_stands.append(nearest_node)

            elif amenity in ['fuel', 'charging_station']:
                existing = get_station_entry(nearest_node)
                feature_type = StationType.COMBUSTION if amenity == 'fuel' else StationType.ELECTRIC

                if existing:
                    if existing['type'] != feature_type and existing['type'] != StationType.MIXED:
                        existing['type'] = StationType.MIXED
                else:
                    stations_data.append({'node_id': nearest_node, 'type': feature_type})

    except Exception as e:
        print(f"Could not fetch POI data from OSM: {e}")

    return stations_data, list(set(taxi_stands))
