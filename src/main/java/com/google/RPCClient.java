package com.google;

import io.grpc.*;

import java.io.File;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.grpc.alts.AltsChannelBuilder;
import services.hello.GreeterGrpc;
import services.hello.HelloReply;
import services.hello.HelloRequest;

/**
 * This RPCClient exists to test the RPCServer, and will be removed when the front-end is developed.
 * <p>
 * The RPCClient begins by initializing a blocking stub for each service.
 *  - A stub allows us to call a service's methods.
 *  - A "blocking" stub means the service handle requests synchronously.
 * <p>
 *  We create a ManagedChannel which specifies the address and port to connect to.
 * <p>
 *  The program arguments specify which methods to test!
 */
public class RPCClient {
  private static final Logger logger = Logger.getLogger(RPCClient.class.getName());

  private final GreeterGrpc.GreeterBlockingStub greeterBlockingStub;

  public RPCClient(Channel channel) {
    greeterBlockingStub = GreeterGrpc.newBlockingStub(channel);
  }

  public void sayHello() {
    HelloRequest request = HelloRequest
            .newBuilder()
            .setName("Spencer")
            .build();

    HelloReply reply;

    try {
      reply = greeterBlockingStub.sayHello(request);
      logger.info("Getting reply.");
    } catch (StatusRuntimeException e) {
      logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
      return;
    }

    logger.info("Response: " + reply);

  }

  public static void main(String[] args) throws Exception {
    String target = "gcloud-grpc-test-tjdrpncpkq-uc.a.run.app:8080";

//    ChannelCredentials credentials = TlsChannelCredentials.newBuilder()
//            .trustManager(caCert)
//            .keyManager(clientCert, clientKey) // client cert
//            .build();
//    ManagedChannel channel = Grpc
//            .newChannelBuilderForAddress("localhost", 50051, credentials)
//            .build();
//
    ManagedChannel channel = AltsChannelBuilder
                    .forTarget(target)
                    .build();
//    ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
//            .build(); // TODO: Change this before deploying to production!

//


    logger.info("Creating client...");
    try {
      RPCClient client = new RPCClient(channel);

      logger.info("Saying hello...");
      client.sayHello();

      /*switch (args[0]) {
        case "0":
          client.sayHello();
          break;

        case "1":
          client.getMenu();
          break;

        case "2":
          client.modifyMenu();
          break;

        case "3":
          client.getPromotions();
          break;

        case "4":
          client.getRewards();
          break;

        default:
          System.out.println("That's not a valid choice.");
          break;
      }*/
    } finally {
      channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }
  }
}
