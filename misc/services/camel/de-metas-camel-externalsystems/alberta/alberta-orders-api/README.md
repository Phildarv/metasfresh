# swagger-java-client

Aufträge - Warenwirtschaft (Basis)
- API version: 1.0.3
  - Build date: 2021-03-27T08:16:46.412Z[GMT]

Synchronisation der Bestellungen aus Alberta mit den Aufträgen mit der Warenwirtschaft


*Automatically generated by the [Swagger Codegen](https://github.com/swagger-api/swagger-codegen)*


## Requirements

Building the API client library requires:
1. Java 1.7+
2. Maven/Gradle

## Installation

To install the API client library to your local Maven repository, simply execute:

```shell
mvn clean install
```

To deploy it to a remote Maven repository instead, configure the settings of the repository and execute:

```shell
mvn clean deploy
```

Refer to the [OSSRH Guide](http://central.sonatype.org/pages/ossrh-guide.html) for more information.

### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
  <groupId>io.swagger</groupId>
  <artifactId>swagger-java-client</artifactId>
  <version>1.0.0</version>
  <scope>compile</scope>
</dependency>
```

### Gradle users

Add this dependency to your project's build file:

```groovy
compile "io.swagger:swagger-java-client:1.0.0"
```

### Others

At first generate the JAR by executing:

```shell
mvn clean package
```

Then manually install the following JARs:

* `target/swagger-java-client-1.0.0.jar`
* `target/lib/*.jar`

## Getting Started

Please follow the [installation](#installation) instruction and execute the following Java code:

```java
import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.OrderApi;

import java.io.File;
import java.util.*;

public class OrderApiExample {

    public static void main(String[] args) {
        
        OrderApi apiInstance = new OrderApi();
        Order body = new Order(); // Order | Die Bestellung
        String apiKey = "apiKey_example"; // String | 
        try {
            OrderMapping result = apiInstance.addOrder(body, apiKey);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling OrderApi#addOrder");
            e.printStackTrace();
        }
    }
}
import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.OrderApi;

import java.io.File;
import java.util.*;

public class OrderApiExample {

    public static void main(String[] args) {
        
        OrderApi apiInstance = new OrderApi();
        String apiKey = "apiKey_example"; // String | 
        String status = "status_example"; // String | created (später ggf. archived) -
        String updatedAfter = "updatedAfter_example"; // String | 2018-02-21T09:30:00.000Z (im UTC-Format)
        try {
            ArrayOfOrders result = apiInstance.getCreatedOrders(apiKey, status, updatedAfter);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling OrderApi#getCreatedOrders");
            e.printStackTrace();
        }
    }
}
```

## Documentation for API Endpoints

All URIs are relative to *https://virtserver.swaggerhub.com/it-labs.de/AuftragWawi/1.0.1*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*OrderApi* | [**addOrder**](docs/OrderApi.md#addOrder) | **POST** /order | Auftrag hinzufügen
*OrderApi* | [**getCreatedOrders**](docs/OrderApi.md#getCreatedOrders) | **GET** /order | Bestellungen je nach Status abrufen
*OrderMappingApi* | [**getOrderMapping**](docs/OrderMappingApi.md#getOrderMapping) | **GET** /order/orderMapping | Zuordnung Auftrag (WaWi) zu Bestellung (Alberta) abrufen
*OrderStatusApi* | [**updateOrderStatus**](docs/OrderStatusApi.md#updateOrderStatus) | **PATCH** /order | Auftragsstatus (ggf. später auch Rezeptstatus) ändern

## Documentation for Models

 - [ArrayOfOrders](docs/ArrayOfOrders.md)
 - [Order](docs/Order.md)
 - [OrderDeliveryAddress](docs/OrderDeliveryAddress.md)
 - [OrderMapping](docs/OrderMapping.md)
 - [OrderStatus](docs/OrderStatus.md)
 - [OrderedArticleLine](docs/OrderedArticleLine.md)
 - [OrderedArticleLineDuration](docs/OrderedArticleLineDuration.md)

## Documentation for Authorization

All endpoints do not require authorization.
Authentication schemes defined for the API:

## Recommendation

It's recommended to create an instance of `ApiClient` per thread in a multithreaded environment to avoid any potential issues.

## Author


