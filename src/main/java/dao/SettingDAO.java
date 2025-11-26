package dao;

import model.Setting;
import utils.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class SettingDAO {
    private final DBUtil jdbcUtil;

    public SettingDAO() {
        jdbcUtil = new DBUtil();
    }

    public boolean updateSetting(Setting setting) {
        try (Connection connection = jdbcUtil.getConnection()) {
            StringBuilder sql = new StringBuilder("UPDATE setting SET ");
            List<Object> params = new ArrayList<>();

            if (setting.getName() != null) {
                sql.append("setting_name = ?, ");
                params.add(setting.getName());
            }
            if (setting.getTypeId() != null) {
                sql.append("type_id = ?, ");
                params.add(setting.getTypeId());
            }
            if (setting.getPriority() != null) {
                sql.append("priority = ?, ");
                params.add(setting.getPriority());
            }
            if (setting.getValue() != null) {
                sql.append("value = ?, ");
                params.add(setting.getValue());
            }
            if (setting.isStatus() != null) {
                sql.append("status = ?, ");
                params.add(setting.isStatus());
            }
            if (setting.getDescription() != null) {
                sql.append("description = ?, ");
                params.add(setting.getDescription());
            }
            if (params.isEmpty()) {
                return false;
            }
            sql.setLength(sql.length() - 2);
            sql.append(" WHERE setting_id = ?");
            params.add(setting.getId());

            PreparedStatement statement = connection.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }
            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Setting findById(int id) {
        try (Connection connection = jdbcUtil.getConnection()) {
            String sql = "SELECT * FROM setting WHERE setting_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Setting setting = new Setting();
                setting.setId(resultSet.getInt("setting_id"));
                setting.setName(resultSet.getString("setting_name"));
                setting.setTypeId((Integer) resultSet.getObject("type_id"));
                setting.setPriority((Integer) resultSet.getObject("priority"));
                setting.setValue(resultSet.getString("value"));
                setting.setStatus((Boolean) resultSet.getObject("status"));
                setting.setDescription(resultSet.getString("description"));

                return setting;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Setting> findFiltered(String type, String status, String search, int page, String sortField, String sortOrder) {
        List<Setting> list = new ArrayList<>();
        try (Connection connection = jdbcUtil.getConnection()) {
            int pageSize = 5;
            int offset = (page - 1) * pageSize;

            StringBuilder sql = new StringBuilder("SELECT s.*, t.setting_name AS type_name FROM setting s LEFT JOIN setting t ON s.type_id = t.setting_id WHERE 1=1");

            List<Object> params = new ArrayList<>();

            if (type != null && !type.isEmpty()) {
                sql.append(" AND s.type_id = ?");
                params.add(Integer.parseInt(type));
            }

            if (status != null && !status.isEmpty()) {
                sql.append(" AND s.status = ?");
                params.add(status.equals("1"));
            }

            if (search != null && !search.isEmpty()) {
                sql.append(" AND s.setting_name LIKE ?");
                params.add("%" + search + "%");
            }

            String safeField;
            if ("name".equalsIgnoreCase(sortField)) {
                safeField = "s.setting_name";
            }
            else if ("type".equalsIgnoreCase(sortField)) {
                safeField = "type_name";
            }
            else if ("priority".equalsIgnoreCase(sortField)) {
                safeField = "s.priority";
            }
            else if ("value".equalsIgnoreCase(sortField)) {
                safeField = "s.value";
            }
            else {
                safeField = "s.setting_id";
            }

            String order = "desc".equalsIgnoreCase(sortOrder) ? "DESC" : "ASC";
            sql.append(" ORDER BY ").append(safeField).append(" ").append(order);


            sql.append(" LIMIT ? OFFSET ?");
            params.add(pageSize);
            params.add(offset);

            PreparedStatement statement = connection.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Setting setting = new Setting();
                setting.setId(resultSet.getInt("setting_id"));
                setting.setName(resultSet.getString("setting_name"));
                setting.setTypeId((Integer) resultSet.getObject("type_id"));
                setting.setTypeName(resultSet.getString("type_name"));
                setting.setPriority((Integer) resultSet.getObject("priority"));
                setting.setValue(resultSet.getString("value"));
                setting.setStatus((Boolean) resultSet.getObject("status"));
                setting.setDescription(resultSet.getString("description"));
                list.add(setting);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    public int getTotalPages(String type, String status, String search) {
        List<Object> params = new ArrayList<>();
        int total = 0;
        try (Connection connection = jdbcUtil.getConnection()) {
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM setting WHERE 1=1");

            if (type != null && !type.isEmpty() && !"All".equalsIgnoreCase(type)) {
                sql.append(" AND type_id = ?");
                params.add(Integer.parseInt(type));
            }

            if (status != null && !status.isEmpty() && !"All".equalsIgnoreCase(status)) {
                sql.append(" AND status = ?");
                params.add(status.equals("1"));
            }

            if (search != null && !search.isEmpty()) {
                sql.append(" AND setting_name LIKE ?");
                params.add("%" + search + "%");
            }

            PreparedStatement statement = connection.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                total = resultSet.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (int) Math.ceil((double) total / 5);
    }

    public List<Setting> findAllTypes() {
        List<Setting> list = new ArrayList<>();
        try (Connection connection = jdbcUtil.getConnection()) {
            String sql = "SELECT setting_id, setting_name FROM setting WHERE type_id IS NULL";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Setting s = new Setting();
                s.setId(resultSet.getInt("setting_id"));
                s.setName(resultSet.getString("setting_name"));
                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean addSetting(Setting setting) {
        try (Connection connection = jdbcUtil.getConnection()) {
            String sql = "INSERT INTO setting(setting_name, type_id, priority, value, status, description) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, setting.getName());
            if (setting.getTypeId() == null) {
                statement.setNull(2, Types.INTEGER);
            }
            else {
                statement.setInt(2, setting.getTypeId());
            }
            if (setting.getPriority() == null) {
                statement.setNull(3, Types.INTEGER);
            }
            else {
                statement.setInt(3, setting.getPriority());
            }
            statement.setString(4, setting.getValue());
            statement.setBoolean(5, setting.isStatus());
            statement.setString(6, setting.getDescription());

            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean existsByName(String name) {
        try (Connection connection = jdbcUtil.getConnection()) {
            String sql = "SELECT COUNT(*) FROM setting WHERE LOWER(setting_name) = LOWER(?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existsByNameExceptId(String name, int id) {
        try (Connection connection = jdbcUtil.getConnection()) {
            String sql = "SELECT COUNT(*) FROM setting WHERE LOWER(setting_name) = LOWER(?) AND setting_id <> ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.setInt(2, id);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
