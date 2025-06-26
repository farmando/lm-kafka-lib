package br.com.leroy.merlin.consumer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@org.springframework.kafka.annotation.KafkaListener
public @interface KafkaListener {
  String topic();
  String groupId() default "";
  String concurrency() default "1";
}
