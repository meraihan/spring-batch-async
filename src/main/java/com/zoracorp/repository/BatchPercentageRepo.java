package com.zoracorp.repository;

import com.zoracorp.model.BatchPercentage;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Repository
public class BatchPercentageRepo {

    private final JdbcTemplate jdbcTemplate;

    public BatchPercentageRepo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public BatchPercentage add(BatchPercentage percentage) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("batch_percentage")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("batch_id", percentage.getBatchId());
        parameterMap.put("percentage", percentage.getPercentage());
        try{
            Number autoGenId = simpleJdbcInsert.executeAndReturnKey(parameterMap);
            if(autoGenId !=null){
                percentage.setId(autoGenId.intValue());
                return percentage;
            }
        }catch (DataAccessException dae){
            dae.printStackTrace();
            return null;
        }
        return percentage;
    }

    public BatchPercentage findByBatchId(String batchId) {
        String query = "SELECT id, batch_id, percentage FROM batch_percentage WHERE batch_id = ?";
        try{
            return jdbcTemplate.queryForObject(query, new Object[]{batchId}, new RowMapper<BatchPercentage>() {
                @Override
                public BatchPercentage mapRow(ResultSet resultSet, int i) throws SQLException {
                    BatchPercentage percentage = new BatchPercentage();
                    percentage.setId(resultSet.getLong("id"));
                    percentage.setBatchId(resultSet.getString("batch_id"));
                    percentage.setPercentage(resultSet.getInt("percentage"));
                    return percentage;
                }
            });
        } catch (DataAccessException dae){
            return new BatchPercentage();
        }
    }

    public boolean update(BatchPercentage percentage) {
        String query = "UPDATE batch_percentage SET batch_id=?, percentage=? WHERE id=?";
        try {
            return jdbcTemplate.update(query, percentage.getBatchId(), percentage.getPercentage(), percentage.getId()) == 1;
        } catch (DataAccessException e) {
            return false;
        }
    }
}
