package br.com.leroy.merlin.consumer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@org.springframework.kafka.annotation.KafkaListener
public @interface KafkaListener {
  @AliasFor(annotation = org.springframework.kafka.annotation.KafkaListener.class, attribute = "topics")
  String topic();

  @AliasFor(annotation = org.springframework.kafka.annotation.KafkaListener.class, attribute = "groupId")
  String groupId() default "";

  @AliasFor(annotation = org.springframework.kafka.annotation.KafkaListener.class, attribute = "concurrency")
  String concurrency() default "1";
}
