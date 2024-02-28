//package plugins.APIForGeneratorAI;
//
//import com.google.cloud.aiplatform.v1beta1.EndpointName;
//import com.google.cloud.aiplatform.v1beta1.PredictResponse;
//import com.google.cloud.aiplatform.v1beta1.PredictionServiceClient;
//import com.google.cloud.aiplatform.v1beta1.PredictionServiceSettings;
//import com.google.protobuf.DescriptorProtos;
//import com.google.protobuf.Value;
//import com.google.protobuf.util.JsonFormat;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//// Send a Predict request to a large language model to test a chat prompt
//public class PredictChatPromptSample {
//
//    public static void main(String[] args) throws IOException {
//        // TODO(developer): Replace these variables before running the sample.
//        String instance =
//                "{\n"
//                        + "   \"context\":  \"My name is Ned. You are my personal assistant. My favorite movies"
//                        + " are Lord of the Rings and Hobbit.\",\n"
//                        + "   \"examples\": [ { \n"
//                        + "       \"input\": {\"content\": \"Who do you work for?\"},\n"
//                        + "       \"output\": {\"content\": \"I work for Ned.\"}\n"
//                        + "    },\n"
//                        + "    { \n"
//                        + "       \"input\": {\"content\": \"What do I like?\"},\n"
//                        + "       \"output\": {\"content\": \"Ned likes watching movies.\"}\n"
//                        + "    }],\n"
//                        + "   \"messages\": [\n"
//                        + "    { \n"
//                        + "       \"author\": \"user\",\n"
//                        + "       \"content\": \"Are my favorite movies based on a book series?\"\n"
//                        + "    }]\n"
//                        + "}";
//        String parameters =
//                "{\n"
//                        + "  \"temperature\": 0.3,\n"
//                        + "  \"maxDecodeSteps\": 200,\n"
//                        + "  \"topP\": 0.8,\n"
//                        + "  \"topK\": 40\n"
//                        + "}";
//        String project = "YOUR_PROJECT_ID";
//        String publisher = "google";
//        String model = "chat-bison@001";
//
//        predictChatPrompt(instance, parameters, project, publisher, model);
//    }
//
//    static void predictChatPrompt(
//            String instance, String parameters, String project, String publisher, String model)
//            throws IOException {
//        PredictionServiceSettings predictionServiceSettings =
//                PredictionServiceSettings.newBuilder()
//                        .setEndpoint("us-central1-aiplatform.googleapis.com:443")
//                        .build();
//
//        // Initialize client that will be used to send requests. This client only needs to be created
//        // once, and can be reused for multiple requests.
//        try (PredictionServiceClient predictionServiceClient =
//                     PredictionServiceClient.create(predictionServiceSettings)) {
//            String location = "us-central1";
//            final EndpointName endpointName =
//                    EndpointName.ofProjectLocationPublisherModelName(project, location, publisher, model);
//
//            Value.Builder instanceValue = Value.newBuilder();
//            DescriptorProtos.FeatureSet.JsonFormat.parser().merge(instance, instanceValue);
//            List<Value> instances = new ArrayList<>();
//            instances.add(instanceValue.build());
//
//            Value.Builder parameterValueBuilder = Value.newBuilder();
//            DescriptorProtos.FeatureSet.JsonFormat.parser().merge(parameters, parameterValueBuilder);
//            Value parameterValue = parameterValueBuilder.build();
//
//            PredictResponse predictResponse =
//                    predictionServiceClient.predict(endpointName, instances, parameterValue);
//            System.out.println("Predict Response");
//        }
//    }
//}