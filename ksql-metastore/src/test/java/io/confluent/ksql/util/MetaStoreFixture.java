/*
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */

package io.confluent.ksql.util;

import io.confluent.ksql.function.FunctionRegistry;
import io.confluent.ksql.metastore.MetaStoreImpl;
import io.confluent.ksql.metastore.MutableMetaStore;
import io.confluent.ksql.metastore.model.KeyField;
import io.confluent.ksql.metastore.model.KsqlStream;
import io.confluent.ksql.metastore.model.KsqlTable;
import io.confluent.ksql.metastore.model.KsqlTopic;
import io.confluent.ksql.schema.ksql.LogicalSchema;
import io.confluent.ksql.schema.ksql.types.SqlType;
import io.confluent.ksql.schema.ksql.types.SqlTypes;
import io.confluent.ksql.serde.KsqlSerdeFactory;
import io.confluent.ksql.serde.SerdeOption;
import io.confluent.ksql.serde.json.KsqlJsonSerdeFactory;
import io.confluent.ksql.util.timestamp.MetadataTimestampExtractionPolicy;
import org.apache.kafka.common.serialization.Serdes;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public final class MetaStoreFixture {

  private MetaStoreFixture() {
  }

  public static MutableMetaStore getNewMetaStore(final FunctionRegistry functionRegistry) {
    return getNewMetaStore(functionRegistry, new KsqlJsonSerdeFactory());
  }

  public static MutableMetaStore getNewMetaStore(
      final FunctionRegistry functionRegistry,
      final KsqlSerdeFactory valueSerdeFactory
  ) {
    final MetadataTimestampExtractionPolicy timestampExtractionPolicy
        = new MetadataTimestampExtractionPolicy();

    final MutableMetaStore metaStore = new MetaStoreImpl(functionRegistry);

    final LogicalSchema test1Schema = LogicalSchema.builder()
        .valueField("COL0", SqlTypes.BIGINT)
        .valueField("COL1", SqlTypes.STRING)
        .valueField("COL2", SqlTypes.STRING)
        .valueField("COL3", SqlTypes.DOUBLE)
        .valueField("COL4", SqlTypes.array(SqlTypes.DOUBLE))
        .valueField("COL5", SqlTypes.map(SqlTypes.DOUBLE))
        .build();

    final KsqlTopic ksqlTopic0 = new KsqlTopic(
        "TEST0",
        "test0",
        valueSerdeFactory,
        false
    );

    final KsqlStream<?> ksqlStream0 = new KsqlStream<>(
        "sqlexpression",
        "TEST0",
        test1Schema,
        SerdeOption.none(),
        KeyField.of("COL0", test1Schema.findValueField("COL0").get()),
        timestampExtractionPolicy,
        ksqlTopic0,
        Serdes::String
    );

    metaStore.putSource(ksqlStream0);

    final KsqlTopic ksqlTopic1 = new KsqlTopic(
        "TEST1",
        "test1",
        valueSerdeFactory,
        false
    );

    final KsqlStream<?> ksqlStream1 = new KsqlStream<>("sqlexpression",
        "TEST1",
        test1Schema,
        SerdeOption.none(),
        KeyField.of("COL0", test1Schema.findValueField("COL0").get()),
        timestampExtractionPolicy,
        ksqlTopic1,
        Serdes::String
    );

    metaStore.putSource(ksqlStream1);

    final LogicalSchema test2Schema = LogicalSchema.builder()
        .valueField("COL0", SqlTypes.BIGINT)
        .valueField("COL1", SqlTypes.STRING)
        .valueField("COL2", SqlTypes.STRING)
        .valueField("COL3", SqlTypes.DOUBLE)
        .valueField("COL4", SqlTypes.BOOLEAN)
        .build();

    final KsqlTopic ksqlTopic2 = new KsqlTopic(
        "TEST2",
        "test2",
        valueSerdeFactory,
        false
    );

    final KsqlTable<String> ksqlTable = new KsqlTable<>(
        "sqlexpression",
        "TEST2",
        test2Schema,
        SerdeOption.none(),
        KeyField.of("COL0", test2Schema.findValueField("COL0").get()),
        timestampExtractionPolicy,
        ksqlTopic2,
        Serdes::String
    );

    metaStore.putSource(ksqlTable);

    final SqlType addressSchema = SqlTypes.struct()
        .field("NUMBER", SqlTypes.BIGINT)
        .field("STREET", SqlTypes.STRING)
        .field("CITY", SqlTypes.STRING)
        .field("STATE", SqlTypes.STRING)
        .field("ZIPCODE", SqlTypes.BIGINT)
        .build();

    final SqlType categorySchema = SqlTypes.struct()
        .field("ID", SqlTypes.BIGINT)
        .field("NAME", SqlTypes.STRING)
        .build();

    final SqlType itemInfoSchema = SqlTypes.struct()
        .field("ITEMID", SqlTypes.BIGINT)
        .field("NAME", SqlTypes.STRING)
        .field("CATEGORY", categorySchema)
        .build();

    final LogicalSchema ordersSchema = LogicalSchema.builder()
        .valueField("ORDERTIME", SqlTypes.BIGINT)
        .valueField("ORDERID", SqlTypes.BIGINT)
        .valueField("ITEMID", SqlTypes.STRING)
        .valueField("ITEMINFO", itemInfoSchema)
        .valueField("ORDERUNITS", SqlTypes.INTEGER)
        .valueField("ARRAYCOL", SqlTypes.array(SqlTypes.DOUBLE))
        .valueField("MAPCOL", SqlTypes.map(SqlTypes.DOUBLE))
        .valueField("ADDRESS", addressSchema)
        .build();

    final KsqlTopic ksqlTopicOrders = new KsqlTopic(
        "ORDERS_TOPIC",
        "orders_topic",
        valueSerdeFactory,
        false
    );

    final KsqlStream<?> ksqlStreamOrders = new KsqlStream<>(
        "sqlexpression",
        "ORDERS",
        ordersSchema,
        SerdeOption.none(),
        KeyField.of("ORDERTIME", ordersSchema.findValueField("ORDERTIME").get()),
        timestampExtractionPolicy,
        ksqlTopicOrders,
        Serdes::String
    );

    metaStore.putSource(ksqlStreamOrders);

    final LogicalSchema testTable3 = LogicalSchema.builder()
        .valueField("COL0", SqlTypes.BIGINT)
        .valueField("COL1", SqlTypes.STRING)
        .valueField("COL2", SqlTypes.STRING)
        .valueField("COL3", SqlTypes.DOUBLE)
        .valueField("COL4", SqlTypes.BOOLEAN)
        .build();

    final KsqlTopic ksqlTopic3 = new KsqlTopic(
        "TEST3",
        "test3",
        valueSerdeFactory,
        false
    );

    final KsqlTable<String> ksqlTable3 = new KsqlTable<>(
        "sqlexpression",
        "TEST3",
        testTable3,
        SerdeOption.none(),
        KeyField.of("COL0", testTable3.findValueField("COL0").get()),
        timestampExtractionPolicy,
        ksqlTopic3,
        Serdes::String
    );

    metaStore.putSource(ksqlTable3);

    final SqlType nestedOrdersSchema = SqlTypes.struct()
        .field("ORDERTIME", SqlTypes.BIGINT)
        .field("ORDERID", SqlTypes.BIGINT)
        .field("ITEMID", SqlTypes.STRING)
        .field("ITEMINFO", itemInfoSchema)
        .field("ORDERUNITS", SqlTypes.INTEGER)
        .field("ARRAYCOL", SqlTypes.array(SqlTypes.DOUBLE))
        .field("MAPCOL", SqlTypes.map(SqlTypes.DOUBLE))
        .field("ADDRESS", addressSchema)
        .build();

    final LogicalSchema nestedArrayStructMapSchema = LogicalSchema.builder()
        .valueField("ARRAYCOL", SqlTypes.array(itemInfoSchema))
        .valueField("MAPCOL", SqlTypes.map(itemInfoSchema))
        .valueField("NESTED_ORDER_COL", nestedOrdersSchema)
        .valueField("ITEM", itemInfoSchema)
        .build();

    final KsqlTopic nestedArrayStructMapTopic = new KsqlTopic(
        "NestedArrayStructMap",
        "NestedArrayStructMap_topic",
        valueSerdeFactory,
        false
    );

    final KsqlStream<?> nestedArrayStructMapOrders = new KsqlStream<>(
        "sqlexpression",
        "NESTED_STREAM",
        nestedArrayStructMapSchema,
        SerdeOption.none(),
        KeyField.none(),
        timestampExtractionPolicy,
        nestedArrayStructMapTopic,
        Serdes::String
    );

    metaStore.putSource(nestedArrayStructMapOrders);

    final KsqlTopic ksqlTopic4 = new KsqlTopic(
        "TEST4",
        "test4",
        valueSerdeFactory,
        false
    );

    final KsqlStream<?> ksqlStream4 = new KsqlStream<>(
        "sqlexpression4",
        "TEST4",
        test1Schema,
        SerdeOption.none(),
        KeyField.none(),
        timestampExtractionPolicy,
        ksqlTopic4,
        Serdes::String
    );

    metaStore.putSource(ksqlStream4);

    return metaStore;
  }
}
