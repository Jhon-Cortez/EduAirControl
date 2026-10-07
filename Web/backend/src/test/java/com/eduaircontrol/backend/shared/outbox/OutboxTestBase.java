package com.eduaircontrol.backend.shared.outbox;

import com.eduaircontrol.backend.PostgresTestBase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;

/**
 * Base de los tests del outbox: PostgreSQL y RabbitMQ reales.
 *
 * <p>El relay solo se puede verificar de verdad contra un broker de verdad.
 * Simular {@code RabbitTemplate} no probaria ni la exchange, ni la routing key,
 * ni el sobre canonico, que son justo las tres cosas que hay que
 * asegurar.
 */
public abstract class OutboxTestBase extends PostgresTestBase {

    protected static final RabbitMQContainer RABBIT =
            new RabbitMQContainer("rabbitmq:3.13-alpine");

    protected static final String EXCHANGE = "eduaircontrol.environmental-data.test";
    protected static final String QUEUE = "eduaircontrol.environmental-data.test.consumer";

    @Autowired
    protected OutboxRepository repository;

    @Autowired
    protected OutboxWriter writer;

    @Autowired
    protected OutboxRelay relay;

    @Autowired
    protected RabbitTemplate rabbitTemplate;

    @Autowired
    protected ConnectionFactory connectionFactory;

    @BeforeAll
    static void startRabbit() {
        if (!RABBIT.isRunning()) {
            RABBIT.start();
        }
    }

    @DynamicPropertySource
    static void rabbit(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", RABBIT::getHost);
        registry.add("spring.rabbitmq.port", RABBIT::getAmqpPort);
        registry.add("spring.rabbitmq.username", RABBIT::getAdminUsername);
        registry.add("spring.rabbitmq.password", RABBIT::getAdminPassword);
        registry.add("app.outbox.exchange", () -> EXCHANGE);
        // El relay se dispara de forma explicita en cada test. Con el periodo real
        // entraria en carrera con las aserciones sobre eventos pendientes.
        registry.add("app.outbox.relay-interval-ms", () -> "3600000");
    }

    /**
     * Declara exchange, cola y binding, y purga la cola. El binding cubre
     * {@code environmental.data.*} a proposito: si el productor cambiara la routing key,
     * estos tests dejarian de recibir el mensaje y fallarian.
     */
    @BeforeEach
    void declareTopology() {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        TopicExchange exchange = new TopicExchange(EXCHANGE, true, false);
        Queue queue = QueueBuilder.durable(QUEUE).build();
        admin.declareExchange(exchange);
        admin.declareQueue(queue);
        admin.declareBinding(BindingBuilder.bind(queue).to(exchange).with("environmental.data.*"));
        admin.purgeQueue(QUEUE);
    }

    /**
     * La base de datos es compartida por toda la suite, asi que sin vaciar la
     * tabla cada test veria los eventos que dejaron los anteriores y los conteos
     * no significarian nada.
     */
    @BeforeEach
    void clearOutbox() {
        repository.deleteAll();
    }
}
