"""Request Generator."""
import random
import uuid
from typing import List

from state import State
from structs.Request import Request

class RequestGenerator:
    """Generates requests at Taxi Stands."""

    def __init__(self, generation_prob: float = 0.5, generation_interval: int = 100):
        self.generation_prob     = generation_prob
        self.generation_interval = generation_interval
        self.ticks_since_last    = 0

    def tick(self, state: State) -> List[Request]:
        """Possibly generate new requests."""
        new_requests = []

        self.ticks_since_last += 1

        # Only start new requests if we have stands
        if not state.taxi_stands:
            return []

        # Check interval
        if self.ticks_since_last < self.generation_interval:
            return []

        # Simple probabilistic generation per tick
        if random.random() < self.generation_prob:
            self.ticks_since_last = 0 # Reset
            origin_stand = random.choice(state.taxi_stands)

            # Destination: Random node in graph
            try:
                all_nodes        = list(state.graph.nodes)
                destination_node = random.choice(all_nodes)

                if destination_node != origin_stand.node_id:
                    req_id = f"R-{uuid.uuid4().hex[:6]}"
                    req = Request(
                        id=req_id,
                        origin_node=origin_stand.node_id,
                        destination_node=destination_node,
                        created_time=state.current_time,
                        priority=random.randint(1, 5),
                        max_wait_time=float(random.randint(10, 30)),
                        env_preference=random.choice([True, False])
                    )
                    new_requests.append(req)
            except Exception:
                pass

        return new_requests
