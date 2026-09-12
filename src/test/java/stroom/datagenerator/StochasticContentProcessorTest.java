/*
 * Copyright 2020 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package stroom.datagenerator;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import stroom.datagenerator.config.EventGenConfig;
import stroom.datagenerator.config.StochasticTemplateConfig;
import stroom.datagenerator.config.TemplateConfig;

import java.io.File;
import java.io.StringWriter;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class StochasticContentProcessorTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void doesNotEmitEventAtOrAfterEndTime() throws Exception {
        File templateFile = temporaryFolder.newFile("event.txt");
        Files.writeString(templateFile.toPath(), "event");

        EventGenConfig appConfig = new EventGenConfig(
                Instant.EPOCH,
                Duration.ofSeconds(1),
                0,
                temporaryFolder.getRoot().getAbsolutePath(),
                null,
                StaticTemplateProcessor.FORMAT_ID,
                Duration.ofSeconds(1),
                "UTC",
                "en-GB",
                "UTF-8",
                "example.com",
                1,
                1,
                1,
                Collections.emptyList());
        TemplateConfig templateConfig = new TemplateConfig(
                templateFile.getName(), StaticTemplateProcessor.FORMAT_ID, null, null, null);
        StochasticTemplateConfig stochasticConfig = new StochasticTemplateConfig(templateConfig, 0.001);
        StochasticContentProcessor processor = new StochasticContentProcessor(
                appConfig, Collections.singletonList(stochasticConfig), null, "test", 0);
        ProcessingContext context = new ProcessingContext(
                Instant.EPOCH,
                Collections.singletonList("user1"),
                Collections.singletonList("host1"),
                1,
                1,
                "example.com");
        StringWriter output = new StringWriter();

        ProcessingContext result = processor.process(context, Instant.EPOCH.plusSeconds(1), output);

        assertEquals("", output.toString());
        assertEquals(Instant.EPOCH, result.getTimestamp());
    }
}
