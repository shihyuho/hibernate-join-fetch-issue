package com.example;

import jakarta.persistence.criteria.JoinType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.hibernate.query.criteria.JpaRoot;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class JoinAndFetchWithSingleClauseTest {

  static SessionFactory sessionFactory;

  @BeforeAll
  static void init() {
    sessionFactory = new Configuration().configure().buildSessionFactory();
  }

  @AfterAll
  static void close() {
    sessionFactory.close();
  }

  Session session;
  HibernateCriteriaBuilder cb;
  JpaCriteriaQuery<Customer> query;
  JpaRoot<Customer> from;

  @BeforeEach
  void openSession() {
    session = sessionFactory.openSession();
    cb = session.getCriteriaBuilder();
    query = cb.createQuery(Customer.class);
    from = query.from(Customer.class);
    query.select(from).distinct(true);
  }

  @AfterEach
  void closeSession() {
    session.close();
  }

  @Test
  @DisplayName("fetch 早於 join, 並且有 where 條件")
  void fetchBeforeJoinWithWhereClauseTest() {
    var fetch = from.fetch("addresses", JoinType.INNER);
    var join = from.join("addresses", JoinType.INNER);
    session.createQuery(
      query.where(cb.equal(join.get("city"), "Taipei"))
    ).getResultList();
  }

  @Test
  @DisplayName("fetch 晚於 join, 並且有 where 條件")
  void fetchAfterJoinWithWhereClauseTest() {
    var join = from.join("addresses", JoinType.INNER);
    var fetch = from.fetch("addresses", JoinType.INNER);
    session.createQuery(
      query.where(cb.equal(join.get("city"), "Taipei"))
    ).getResultList();
  }

  @Test
  @DisplayName("fetch 早於 join, 但沒有 where 條件")
  void fetchBeforeJoinWithoutWhereClauseTest() {
    var fetch = from.fetch("addresses", JoinType.INNER);
    var join = from.join("addresses", JoinType.INNER);
    session.createQuery(query).getResultList();
  }

  @Test
  @DisplayName("fetch 晚於 join, 但沒有 where 條件")
  void fetchAfterJoinWithoutWhereClauseTest() {
    var join = from.join("addresses", JoinType.INNER);
    var fetch = from.fetch("addresses", JoinType.INNER);
    session.createQuery(query).getResultList();
  }
}
