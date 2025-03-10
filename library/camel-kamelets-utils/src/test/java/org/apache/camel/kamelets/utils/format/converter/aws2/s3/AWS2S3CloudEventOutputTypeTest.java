/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.camel.kamelets.utils.format.converter.aws2.s3;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.apache.camel.CamelContextAware;
import org.apache.camel.Exchange;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.kamelets.utils.format.DefaultDataTypeRegistry;
import org.apache.camel.kamelets.utils.format.converter.utils.CloudEvents;
import org.apache.camel.kamelets.utils.format.spi.DataTypeConverter;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AWS2S3CloudEventOutputTypeTest {

    private final DefaultCamelContext camelContext = new DefaultCamelContext();

    private final AWS2S3CloudEventOutputType outputType = new AWS2S3CloudEventOutputType();

    @Test
    void shouldMapToCloudEvent() throws Exception {
        Exchange exchange = new DefaultExchange(camelContext);

        exchange.getMessage().setHeader(AWS2S3Constants.KEY, "test1.txt");
        exchange.getMessage().setHeader(AWS2S3Constants.BUCKET_NAME, "myBucket");
        exchange.getMessage().setHeader(AWS2S3Constants.CONTENT_TYPE, "text/plain");
        exchange.getMessage().setHeader(AWS2S3Constants.CONTENT_ENCODING, StandardCharsets.UTF_8.toString());
        exchange.getMessage().setBody(new ByteArrayInputStream("Test1".getBytes(StandardCharsets.UTF_8)));
        outputType.convert(exchange);

        Assertions.assertTrue(exchange.getMessage().hasHeaders());
        Assertions.assertTrue(exchange.getMessage().getHeaders().containsKey(AWS2S3Constants.KEY));
        assertEquals("org.apache.camel.event.aws.s3.getObject", exchange.getMessage().getHeader(CloudEvents.CAMEL_CLOUD_EVENT_TYPE));
        assertEquals("test1.txt", exchange.getMessage().getHeader(CloudEvents.CAMEL_CLOUD_EVENT_SUBJECT));
        assertEquals("aws.s3.bucket.myBucket", exchange.getMessage().getHeader(CloudEvents.CAMEL_CLOUD_EVENT_SOURCE));
    }

    @Test
    public void shouldLookupDataType() throws Exception {
        DefaultDataTypeRegistry dataTypeRegistry = new DefaultDataTypeRegistry();
        CamelContextAware.trySetCamelContext(dataTypeRegistry, camelContext);
        Optional<DataTypeConverter> converter = dataTypeRegistry.lookup("aws2-s3", "cloudevents");
        Assertions.assertTrue(converter.isPresent());
    }
}
