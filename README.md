# Overview

Application gathering Ethereum transfers in a graph and using it to calculate a trust between addresses.

# Usage

## Starting ethereum node
Start a local Ethereum node and make sure it support filters, e.g.:
```
parity --chain ropsten
```

## Configure the application

If Ethereum node's URL is not `http://127.0.0.1:8545`, then set the correct URL using `calculator.nodeUrl` property.

If you prefer to not load blocks from zero, but rather from a different number, use `calculator.firstBlockNumber`
property. 

## Run tests
```
./mvnw clean verify
```

## Start the application
```
./mvnw clean spring-boot:run
```

Invoke the service
```
curl "localhost:8080/trust?from=0x0123456789012345678901234567890123456789&to=0x0123456789012345678901234567890123456789"
```

Block address from trust calculations
```
curl -X PUT "localhost:8080/users/0x0123456789012345678901234567890123456789/block"
```

Unblock the address
```
curl -X DELETE "localhost:8080/users/0x08ae99bffca7d09334581244a7b494d592cc2fd17/block"
```