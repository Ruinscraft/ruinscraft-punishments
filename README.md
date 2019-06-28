# ruinscraft-punishments
A punishments plugin for Ruinscraft

Handles cross server
- kicks
- warns
- mutes
- bans

via messaging implemented with Redis

# Requirements
- A MySQL server
- A Redis server
- Ruinscraft Paper (which includes Jedis library)

# Website
The website will be written in PHP. It is a WIP.

# BanManager import
I wrote this to import old BanManager punishments. Requires `mysql-connector-java` to be installed on a RedHat based Linux system. Run the `import.sh` script to automatically import BanManager punishments.
