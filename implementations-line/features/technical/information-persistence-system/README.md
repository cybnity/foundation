## PURPOSE
This types of technical features are providing persistence services allowing to store and to retrieve states of several types of data (e.g domain events, domain entities) according to specific storage technologies.

# PERSISTENCE COMPONENTS
- [redis-events-store](redis-events-store): event storage technology allowing to maintain persistent streams of events (e.g logs, domain data write-model items) in a append-only approach. This type of persistence system is implementing Redis persistent streams and optional backup system (e.g to standard database ensuring recovery in case of restart/recovery of Redis distributed cluster).
