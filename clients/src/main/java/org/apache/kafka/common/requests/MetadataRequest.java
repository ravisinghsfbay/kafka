/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.kafka.common.requests;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.common.protocol.ApiKeys;
import org.apache.kafka.common.protocol.Errors;
import org.apache.kafka.common.protocol.ProtoUtils;
import org.apache.kafka.common.protocol.types.Schema;
import org.apache.kafka.common.protocol.types.Struct;

public class MetadataRequest extends AbstractRequest {
    
    private static final Schema CURRENT_SCHEMA = ProtoUtils.currentRequestSchema(ApiKeys.METADATA.id);
    private static final String TOPICS_KEY_NAME = "topics";

    private final List<String> topics;

    public MetadataRequest(List<String> topics) {
        super(new Struct(CURRENT_SCHEMA));
        struct.set(TOPICS_KEY_NAME, topics.toArray());
        this.topics = topics;
    }

    public MetadataRequest(Struct struct) {
        super(struct);
        Object[] topicArray = struct.getArray(TOPICS_KEY_NAME);
        topics = new ArrayList<String>();
        for (Object topicObj: topicArray) {
            topics.add((String) topicObj);
        }
    }

    @Override
    public AbstractRequestResponse getErrorResponse(Throwable e) {
        Map<String, Errors> topicErrors = new HashMap<String, Errors>();
        for (String topic: topics) {
            topicErrors.put(topic, Errors.forException(e));
        }
        return new MetadataResponse(topicErrors);
    }

    public List<String> topics() {
        return topics;
    }

    public static MetadataRequest parse(ByteBuffer buffer) {
        return new MetadataRequest((Struct) CURRENT_SCHEMA.read(buffer));
    }
}
