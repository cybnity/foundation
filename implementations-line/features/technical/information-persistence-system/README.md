## PURPOSE
This types of technical features are providing persistence services allowing to store and to retrieve states of several types of data (e.g domain events, domain entities) according to specific storage technologies.

# PERSISTENCE COMPONENTS
- [redis-events-store](redis-events-store): event storage technology allowing to maintain persistent streams of events (e.g change events; domain data write-model items) in an append-only approach. This type of persistence system is implementing Redis persistent streams with optional backup system (e.g to standard database like MongoDB ensuring recovery in case of restart/recovery of Redis distributed cluster).

- mongodb-backup-db: document storage technology adapter allowing to archive multiple types of data (e.g domain application entity streams; application logs store) and to recover them (e.g in case of disaster). This type of persistence system is implementing MongoDB open source solution and manage the coupling with it (e.g adapter pattern)
