package com.stepuro.payment.order.repository;

import com.stepuro.payment.order.api.exceptions.ResourceNotFoundException;
import com.stepuro.payment.order.model.PaymentOrderEntity;
import com.stepuro.payment.order.model.enums.PaymentOrderEntityStatus;
import com.stepuro.payment.order.model.enums.PaymentType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Repository
public class PaymentOrderEntityRepositoryJdbc {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Transactional
    public UUID save(PaymentOrderEntity paymentOrderEntity){
        paymentOrderEntity.setId(UUID.randomUUID());
        jdbcTemplate.update("INSERT INTO payment_order_entity " +
                        "(id, " +
                        "source_card_number, " +
                        "destination_card_number, " +
                        "source_account_number, " +
                        "destination_account_number, " +
                        "created_date, " +
                        "updated_date, " +
                        "status, " +
                        "payment_type, " +
                        "amount, " +
                        "user_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",

                paymentOrderEntity.getId(),
                paymentOrderEntity.getSourceCardNumber(),
                paymentOrderEntity.getDestinationCardNumber(),
                paymentOrderEntity.getSourceAccountNumber(),
                paymentOrderEntity.getDestinationAccountNumber(),
                paymentOrderEntity.getCreatedDate().toString(),
                paymentOrderEntity.getUpdatedDate().toString(),
                paymentOrderEntity.getStatus().toString(),
                paymentOrderEntity.getPaymentType().toString(),
                paymentOrderEntity.getAmount(),
                paymentOrderEntity.getUserId());

        return paymentOrderEntity.getId();
    }

    @Transactional
    public int edit(PaymentOrderEntity paymentOrderEntity){
        return jdbcTemplate.update("UPDATE payment_order_entity " +
                        "SET source_card_number = ?, " +
                        "destination_card_number = ?, " +
                        "source_account_number = ?, " +
                        "destination_account_number = ?, " +
                        "created_date = ?, " +
                        "updated_date = ?, " +
                        "status = ?, " +
                        "payment_type = ?, " +
                        "amount = ?, " +
                        "user_id = ? " +
                        "WHERE payment_order_entity.id = ? ",
                paymentOrderEntity.getSourceCardNumber(),
                paymentOrderEntity.getDestinationCardNumber(),
                paymentOrderEntity.getSourceAccountNumber(),
                paymentOrderEntity.getDestinationAccountNumber(),
                paymentOrderEntity.getCreatedDate().toString(),
                paymentOrderEntity.getUpdatedDate().toString(),
                paymentOrderEntity.getStatus().toString(),
                paymentOrderEntity.getPaymentType().toString(),
                paymentOrderEntity.getAmount(),
                paymentOrderEntity.getUserId(),
                paymentOrderEntity.getId());
    }

    public List<PaymentOrderEntity> findAll(){
        return jdbcTemplate.query("SELECT * " +
                        "FROM payment_order_entity ",
                new PaymentOrderEntityMapper());
    }

    public PaymentOrderEntity findById(UUID id){
        try {
            return jdbcTemplate.queryForObject("SELECT * " +
                            "FROM payment_order_entity " +
                            "WHERE payment_order_entity.id = ?",
                    new PaymentOrderEntityMapper(),
                    id);
        }
        catch (EmptyResultDataAccessException exception){
            throw new ResourceNotFoundException("PaymentOrderEntity with id " + id + " not found");
        }
    }

    @Transactional
    public void deleteById(UUID id){
        jdbcTemplate.update("DELETE FROM payment_order_entity where id = ?", id);
    }

    private static final class PaymentOrderEntityMapper implements RowMapper<PaymentOrderEntity> {

        @Override
        public PaymentOrderEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            PaymentOrderEntity paymentOrderEntity = new PaymentOrderEntity();
            paymentOrderEntity.setId(UUID.fromString(rs.getString("id")));
            paymentOrderEntity.setSourceCardNumber(rs.getString("source_card_number"));
            paymentOrderEntity.setDestinationCardNumber(rs.getString("destination_card_number"));
            paymentOrderEntity.setSourceAccountNumber(rs.getString("source_account_number"));
            paymentOrderEntity.setDestinationAccountNumber(rs.getString("destination_account_number"));
            paymentOrderEntity.setCreatedDate(rs.getTimestamp("created_date"));
            paymentOrderEntity.setUpdatedDate(rs.getTimestamp("updated_date"));
            paymentOrderEntity.setStatus(PaymentOrderEntityStatus.valueOf(rs.getString("status")));
            paymentOrderEntity.setPaymentType(PaymentType.valueOf(rs.getString("payment_type")));
            paymentOrderEntity.setAmount(rs.getBigDecimal("amount"));
            paymentOrderEntity.setUserId(rs.getInt("user_id"));

            return paymentOrderEntity;
        }
    }
}
