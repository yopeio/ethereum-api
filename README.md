# ethereum-api
Java client of JSON RPC Ethereum API, based on  reference.  



### Prerequisities
- java 8
- maven 3.x
- a local/remote ethereum blockchain based on  https://github.com/ethereum/wiki/wiki/JSON-RPC reference.

### Configuration
change the ethereum blockchain address in `yope-ethereum-rest\application.yml`

`org.ethereum.address: http:/...`

### How to build and run it
`cd yope-ethereum-rest`

`mvn spring-boot:run`

### RESTful APIs

#### Account balance:
Returns the balance of the account (in szabo) of given address.

##### Path
`GET` /accounts/${accountAddress}/balance

##### Parameters
* `accountAddress`: address of the account  

```
curl -X GET -H "Content-Type: application/json" "http://localhost:8080/accounts/0x03733b713032e9040d04acd4720bedaa717378df/balance"
```
```
{"response":825000000,"responseCode":200,"message":"OK"}%  
```

#### Contract creation:
Creates a contract into the blockchain:

##### Path
`POST` /contracts

##### Parameters
* `solidityContract`: content of the Solidity Contract
* `accountAddress`: address of the account

```
curl -X POST -H "Content-Type: application/json"  -d '{
    "solidityContract": "contract SimpleStorage { uint storedData; function set(uint x) { storedData = x; } function get() constant returns (uint retVal) { return storedData; } }",
    "accountAddress": "0x03733b713032e9040d04acd4720bedaa717378df"
}' "http://localhost:8080/contracts"
```

```
{
  "response": {
    "SimpleStorage": {
      "transactionHash": "0x87aad9043516c0c5192515eb7ec5d62619f73fec6e666b367a8796c5d7e587d4",
      "contractAddress": "0x8f7de5360431abfd5422aac1888c1e9902e6b543",
      "blockHash": "0x748a4654727b8edc0410735b866c928e20600efbd99107307348e851757dd81e",
      "transactionIndex": 0,
      "blockNumber": 165,
      "cumulativeGasUsed": 37558,
      "gasUsed": 37558
    }
  },
  "responseCode": 200,
  "message": "OK"
}
```

#### Contract update:
Modifies the state of a contract
##### Path
`PUT` /contracts/${contractAddress}

##### Parameters
* `solidityContract`: content of the Solidity Contract
* `accountAddress`: address of the account
* `contractKey`: Solidity contract name
* `method`: method to modify
* `args`: array of method's input parameters

```
curl -X PUT -H "Content-Type: application/json" -H "Cache-Control: no-cache" -H "Postman-Token: 90518cc7-f2ef-345b-c1a3-c7e5f022aa3b" -d '{
    "solidityContract": "contract SimpleStorage { uint storedData; function set(uint x) { storedData = x; } function get() constant returns (uint retVal) { return storedData; } }",
    "accountAddress": "0x03733b713032e9040d04acd4720bedaa717378df",
    "contractKey": "SimpleStorage",
    "method": "set",
    "args": ["53"]
}' "http://localhost:8080/contracts/0xfdd64188d6134f955dda2bd55234acefa2779da6"
```

#### Contract run:
Executes a contract
##### Path
`POST` /contracts/${contractAddress}

##### Parameters
* `solidityContract`: content of the Solidity Contract
* `accountAddress`: address of the account
* `contractKey`: Solidity contract name
* `method`: method to execute

```
curl -X POST -H "Content-Type: application/json" -H "Cache-Control: no-cache" -H "Postman-Token: 0b8745d2-ebaa-cd6c-da82-4f075c47e5bb" -d '{
    "solidityContract": "contract SimpleStorage { uint storedData; function set(uint x) { storedData = x; } function get() constant returns (uint retVal) { return storedData; } }",
    "accountAddress": "0x03733b713032e9040d04acd4720bedaa717378df",
    "contractKey": "SimpleStorage",
    "method": "get"
}' "http://localhost:8080/contracts/0xfdd64188d6134f955dda2bd55234acefa2779da6"
```

#### License
The ethereum-api is released under the MIT permissive free software license.
