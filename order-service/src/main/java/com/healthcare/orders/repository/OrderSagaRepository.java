package com.healthcare.orders.repository;

import com.healthcare.orders.model.OrderSaga;
import org.springframework.data.cassandra.repository.CassandraRepository;
import java.util.UUID;

public interface OrderSagaRepository extends CassandraRepository<OrderSaga, UUID> {
}