package benchmark;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.json.async.NonBlockingJsonParser;
import com.fasterxml.jackson.databind.*;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

class Empty {
}

class Event {
    public Request request;
}

class Request {
    public Response response;
}

class Response {
    public List<Item> items;
}

class Item {
    public String key;
}

public class JacksonBenchmark {
    static String readJson(String path) {
        try {
            return Files.readString(Paths.get(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        final ObjectMapper mapper = new ObjectMapper();

        {
            mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
            mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        }

        final JsonFactory jsonFactory = mapper.getFactory();
        final String json = readJson("./src/jmh/resources/scrambled.json");
        final byte[] jsonBytes = json.getBytes();
    }

    @Benchmark
    @Fork(value = 1, warmups = 0)
    @Warmup(iterations = 1)
    @Measurement(iterations = 1, time = 60)
    @BenchmarkMode(Mode.Throughput)
    public void defaultParser(Blackhole blackhole, BenchmarkState state) throws IOException {
        final JsonParser jsonParser = state.jsonFactory.createParser(state.jsonBytes);
        final Event result = jsonParser.readValueAs(Event.class);
        blackhole.consume(result);
    }

    @Benchmark
    @Fork(value = 1, warmups = 0)
    @Warmup(iterations = 1)
    @Measurement(iterations = 1, time = 60)
    @BenchmarkMode(Mode.Throughput)
    public void defaultParserEmpty(Blackhole blackhole, BenchmarkState state) throws IOException {
        final JsonParser jsonParser = state.jsonFactory.createParser(state.jsonBytes);
        final Empty result = jsonParser.readValueAs(Empty.class);
        blackhole.consume(result);
    }

    @Benchmark
    @Fork(value = 1, warmups = 0)
    @Warmup(iterations = 1)
    @Measurement(iterations = 1, time = 60)
    @BenchmarkMode(Mode.Throughput)
    public void nonBlockingParser(Blackhole blackhole, BenchmarkState state) throws IOException {
        final NonBlockingJsonParser jsonParser = (NonBlockingJsonParser) state.jsonFactory.createNonBlockingByteArrayParser();
        final byte[] bytes = state.jsonBytes;
        jsonParser.feedInput(bytes, 0, bytes.length);

        int i = 0;
        while (jsonParser.nextToken() != JsonToken.NOT_AVAILABLE) {
            i++;
        }

        blackhole.consume(i);
    }
}
