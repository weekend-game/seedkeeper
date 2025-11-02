package game.weekend.seedkeeper.journals;

import java.io.ByteArrayInputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import game.weekend.seedkeeper.db.ComboItem;
import game.weekend.seedkeeper.db.DBTables;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Seed
 */
public class SeedTables extends DBTables {

	public SeedTables(Connection c) {
		try {
			setFieldsLength(c);

		} catch (SQLException e) {
			System.out.println("SeedTables.SeedTables() - " + e);
		}
	}

	private void setFieldsLength(Connection c) throws SQLException {
		Statement s = c.createStatement();
		ResultSet rs = s.executeQuery("SELECT name, article, description, vegetation, mass, height, yield, "
				+ " length, sowing_time, transplant_time, in_ground, planting_scheme, ground FROM Seeds s WHERE 0=1");
		ResultSetMetaData metaData = rs.getMetaData();

		int ind = 0;
		Seed.setNAME_LENGTH(metaData.getPrecision(++ind));
		Seed.setARTICLE_LENGTH(metaData.getPrecision(++ind));
		Seed.setDESCRIPTION_LENGTH(metaData.getPrecision(++ind));
		Seed.setVEGETATION_LENGTH(metaData.getPrecision(++ind));
		Seed.setMASS_LENGTH(metaData.getPrecision(++ind));
		Seed.setHEIGHT_LENGTH(metaData.getPrecision(++ind));
		Seed.setYIELD_LENGTH(metaData.getPrecision(++ind));
		Seed.setLENGTH_LENGTH(metaData.getPrecision(++ind));
		Seed.setSOWING_TIME_LENGTH(metaData.getPrecision(++ind));
		Seed.setTRANSPLANT_TIME_LENGTH(metaData.getPrecision(++ind));
		Seed.setIN_GROUND_LENGTH(metaData.getPrecision(++ind));
		Seed.setPLANTING_SCHEME_LENGTH(metaData.getPrecision(++ind));
		Seed.setGROUND_LENGTH(metaData.getPrecision(++ind));

		rs.close();
		s.close();
	}

	public ObservableList<Seed> getList(int category_id) {
		ObservableList<Seed> list = FXCollections.observableArrayList();

		Connection c = getConnection();
		if (c == null)
			return list;

		try (Statement s = c.createStatement()) {
			String req = "SELECT s.id, s.mark, s.name, s.article, s.use_by, " + " b.name AS brand, s.vegetation, "
					+ " st.name AS status, s.hybrid, st.color AS status_color, st.id AS status_id, b.id AS brand_id "
					+ " FROM Seeds s " + " LEFT OUTER JOIN statuses st ON st.id = s.status_id "
					+ " LEFT OUTER JOIN brands b ON b.id = s.brand_id " + " WHERE 0=0 ";

			if (category_id > 0)
				req = req + " AND category_id = " + category_id;
			req = req + " ORDER BY s.name ";

			ResultSet rs = s.executeQuery(req);

			while (rs.next())
				list.add(new Seed(rs.getInt(1), rs.getBoolean(2), rs.getString(3), rs.getString(4), rs.getInt(5),
						rs.getString(6), rs.getString(7), rs.getString(8), rs.getBoolean(9), rs.getString(10),
						rs.getInt(11), rs.getInt(12)));
		} catch (SQLException e) {
			System.out.println("SeedTables.getList() - " + e);
		}

		return list;
	}

	@Override
	public ObservableList<ComboItem> getListForCombo() {
		return null;
	}

	public void get(Seed seed) {
		if (seed == null)
			return;

		if (seed.getId() == null)
			return;

		Connection c = getConnection();
		if (c == null)
			return;

		try (Statement s = c.createStatement()) {

			ResultSet rs = s.executeQuery("SELECT s.brand_id, s.category_id, s.status_id, s.color_id, s.kind_id, "
					+ " s.name, s.article, s.hybrid, s.use_by, s.description, s.photo, s.vegetation, s.mass, s.height, s.yield, "
					+ " s.length, s.sowing_time, s.transplant_time, s.in_ground, s.planting_scheme, s.ground FROM Seeds s "
					+ " LEFT OUTER JOIN brands b ON b.id = s.brand_id WHERE s.id = " + seed.getId());

			while (rs.next()) {
				seed.setBrand_id(rs.getInt(1));
				seed.setCategory_id(rs.getInt(2));
				seed.setStatus_id(rs.getInt(3));
				seed.setColor_id(rs.getInt(4));
				seed.setKind_id(rs.getInt(5));

				seed.setName(rs.getString(6));
				seed.setArticle(rs.getString(7));
				seed.setHybrid(rs.getBoolean(8));
				seed.setUse_by(rs.getInt(9));
				seed.setDescription(rs.getString(10));

				Blob b = rs.getBlob(11);
				if (b != null) {
					seed.setPhoto(b.getBytes(1L, (int) b.length()));
				}

				seed.setVegetation(rs.getString(12));
				seed.setMass(rs.getString(13));
				seed.setHeight(rs.getString(14));
				seed.setYield(rs.getString(15));

				seed.setLength(rs.getString(16));
				seed.setSowing_time(rs.getString(17));
				seed.setTransplant_time(rs.getString(18));
				seed.setIn_ground(rs.getString(19));
				seed.setPlanting_scheme(rs.getString(20));
				seed.setGround(rs.getString(21));
			}
		} catch (SQLException e) {
			System.out.println("SeedTables.read(Seed seed) - " + e);
		}

	}

	public void add(Seed seed) {
		Connection c = getConnection();
		if (c == null)
			return;

		try {
			PreparedStatement ps = c
					.prepareStatement("INSERT INTO Seeds ( brand_id, category_id, status_id, color_id, kind_id, "
							+ "	name, article, hybrid, use_by, description, photo, vegetation, mass, height, "
							+ "	yield, length, sowing_time, transplant_time, in_ground, planting_scheme, ground "
							+ "	) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 1);

			int ind = 0;
			if (seed.getBrand_id() == null)
				ps.setNull(++ind, Types.INTEGER);
			else
				ps.setInt(++ind, seed.getBrand_id());

			if (seed.getCategory_id() == null)
				ps.setNull(++ind, Types.INTEGER);
			else
				ps.setInt(++ind, seed.getCategory_id());

			if (seed.getStatus_id() == null)
				ps.setNull(++ind, Types.INTEGER);
			else
				ps.setInt(++ind, seed.getStatus_id());

			if (seed.getColor_id() == null)
				ps.setNull(++ind, Types.INTEGER);
			else
				ps.setInt(++ind, seed.getColor_id());

			if (seed.getKind_id() == null)
				ps.setNull(++ind, Types.INTEGER);
			else
				ps.setInt(++ind, seed.getKind_id());

			ps.setString(++ind, seed.getName());
			ps.setString(++ind, seed.getArticle());
			ps.setBoolean(++ind, seed.getHybrid());

			if (seed.getUse_by() == null)
				ps.setNull(++ind, Types.INTEGER);
			else
				ps.setInt(++ind, seed.getUse_by());

			ps.setString(++ind, seed.getDescription());

			if (seed.getPhoto() == null)
				ps.setBlob(++ind, (Blob) null);
			else
				ps.setBlob(++ind, new ByteArrayInputStream(seed.getPhoto()));

			ps.setString(++ind, seed.getVegetation());
			ps.setString(++ind, seed.getMass());
			ps.setString(++ind, seed.getHeight());
			ps.setString(++ind, seed.getYield());
			ps.setString(++ind, seed.getLength());
			ps.setString(++ind, seed.getSowing_time());
			ps.setString(++ind, seed.getTransplant_time());
			ps.setString(++ind, seed.getIn_ground());
			ps.setString(++ind, seed.getPlanting_scheme());
			ps.setString(++ind, seed.getGround());

			ps.executeUpdate();

			seed.setId(null);
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				seed.setId(rs.getInt(1));
			}

			ps.close();
			rs.close();
		} catch (SQLException e) {
			System.out.println("SeedTables.add(Seed seed) - " + e);
		}
	}

	public void set(Seed seed, boolean withPhoto) {
		Connection c = getConnection();
		if (c == null)
			return;

		try {
			PreparedStatement ps;
			if (withPhoto) {
				ps = c.prepareStatement("UPDATE Seeds SET  "
						+ " brand_id = ?, category_id = ?, status_id = ?, color_id = ?, kind_id = ?, "
						+ " name = ?, article = ?, hybrid = ?, use_by = ?, "
						+ " description = ?, photo = ?, vegetation = ?, mass = ?, height = ?, "
						+ " yield = ?, length = ?, sowing_time = ?, transplant_time = ?, in_ground = ?, "
						+ " planting_scheme = ?, ground = ?  WHERE id = ?");
			} else {
				ps = c.prepareStatement("UPDATE Seeds SET  "
						+ " brand_id = ?, category_id = ?, status_id = ?, color_id = ?, kind_id = ?, "
						+ " name = ?, article = ?, hybrid = ?, use_by = ?, "
						+ " description = ?, vegetation = ?, mass = ?, height = ?, "
						+ " yield = ?, length = ?, sowing_time = ?, transplant_time = ?, in_ground = ?, "
						+ " planting_scheme = ?, ground = ?  WHERE id = ?");
			}

			int ind = 0;
			if (seed.getBrand_id() == null)
				ps.setNull(++ind, Types.INTEGER);
			else
				ps.setInt(++ind, seed.getBrand_id());

			if (seed.getCategory_id() == null)
				ps.setNull(++ind, Types.INTEGER);
			else
				ps.setInt(++ind, seed.getCategory_id());

			if (seed.getStatus_id() == null)
				ps.setNull(++ind, Types.INTEGER);
			else
				ps.setInt(++ind, seed.getStatus_id());

			if (seed.getColor_id() == null)
				ps.setNull(++ind, Types.INTEGER);
			else
				ps.setInt(++ind, seed.getColor_id());

			if (seed.getKind_id() == null)
				ps.setNull(++ind, Types.INTEGER);
			else
				ps.setInt(++ind, seed.getKind_id());

			ps.setString(++ind, seed.getName());
			ps.setString(++ind, seed.getArticle());
			ps.setBoolean(++ind, seed.getHybrid());

			if (seed.getUse_by() == null)
				ps.setNull(++ind, Types.INTEGER);
			else
				ps.setInt(++ind, seed.getUse_by());

			ps.setString(++ind, seed.getDescription());

			if (withPhoto) {
				if (seed.getPhoto() == null)
					ps.setBlob(++ind, (Blob) null);
				else
					ps.setBlob(++ind, new ByteArrayInputStream(seed.getPhoto()));
			}

			ps.setString(++ind, seed.getVegetation());
			ps.setString(++ind, seed.getMass());
			ps.setString(++ind, seed.getHeight());
			ps.setString(++ind, seed.getYield());
			ps.setString(++ind, seed.getLength());
			ps.setString(++ind, seed.getSowing_time());
			ps.setString(++ind, seed.getTransplant_time());
			ps.setString(++ind, seed.getIn_ground());
			ps.setString(++ind, seed.getPlanting_scheme());
			ps.setString(++ind, seed.getGround());

			ps.setInt(++ind, seed.getId());

			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SeedTables.set(Seed seed) - " + e);
		}
	}

	public void updateMark(int id, boolean mark) {
		Connection c = getConnection();
		if (c == null)
			return;

		try {
			PreparedStatement ps;
			ps = c.prepareStatement("UPDATE Seeds SET mark = ? WHERE id = ?");
			ps.setBoolean(1, mark);
			ps.setInt(2, id);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SeedTables.updateMark(int id, boolean mark) - " + e);
		}
	}

	public boolean remove(Seed seed) {
		boolean deleted = false;

		Connection c = getConnection();
		if (c == null)
			return deleted;

		try (Statement s = c.createStatement()) {
			s.executeUpdate("DELETE FROM Seeds WHERE id = " + seed.getId());
			deleted = true;
		} catch (SQLException e) {
			System.out.println("SeedTables.remove(Seed seed) - " + e);
		}

		return deleted;
	}
}
