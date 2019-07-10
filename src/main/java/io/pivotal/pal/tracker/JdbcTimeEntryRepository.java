package io.pivotal.pal.tracker;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcTimeEntryRepository implements TimeEntryRepository {

    private JdbcTemplate jdbcTemplate;

    public JdbcTimeEntryRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        PreparedStatementCreator insertStatement = con -> {
            final PreparedStatement ps = con.prepareStatement("INSERT INTO time_entries (project_id, user_id, date, hours) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, timeEntry.getProjectId());
            ps.setLong(2, timeEntry.getUserId());
            ps.setDate(3, Date.valueOf(timeEntry.getDate()));
            ps.setInt(4, timeEntry.getHours());
            return ps;
        };

        jdbcTemplate.update(insertStatement, keyHolder);


        return this.find(keyHolder.getKey().longValue());
    }

    @Override
    public TimeEntry find(long timeEntryId) {
        PreparedStatementCreator selectStatement = con -> {
            final PreparedStatement ps = con.prepareStatement("SELECT * FROM time_entries WHERE id=?");
            ps.setLong(1, timeEntryId);
            return ps;
        };

        return jdbcTemplate.query(selectStatement, rs -> {
            if(rs.next()) {
                return new TimeEntry(rs.getLong(1), rs.getLong(2), rs.getLong(3), rs.getDate(4).toLocalDate(), rs.getInt(5));
            }
            return null;
        });
    }

    @Override
    public List<TimeEntry> list() {

        return jdbcTemplate.query("SELECT * FROM time_entries", rs -> {
            List<TimeEntry> list = new ArrayList();
            while(rs.next()) {
                list.add( new TimeEntry(rs.getLong(1), rs.getLong(2), rs.getLong(3), rs.getDate(4).toLocalDate(), rs.getInt(5)));
                }
            return list;
        });
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        PreparedStatementCreator updateStatement = con -> {
            final PreparedStatement ps = con.prepareStatement("UPDATE time_entries SET project_id=?, user_id=?, date=?, hours=? WHERE id=?");
            ps.setLong(1, timeEntry.getProjectId());
            ps.setLong(2, timeEntry.getUserId());
            ps.setDate(3, Date.valueOf(timeEntry.getDate()));
            ps.setInt(4, timeEntry.getHours());
            ps.setLong(5, id);
            return ps;
        };

        jdbcTemplate.update(updateStatement);
        return this.find(id);
    }

    @Override
    public void delete(long id) {

        PreparedStatementCreator deleteStatement = con -> {
            final PreparedStatement ps = con.prepareStatement("DELETE FROM time_entries WHERE id=?");
            ps.setLong(1, id);
            return ps;
        };
        jdbcTemplate.update(deleteStatement);
    }
}
