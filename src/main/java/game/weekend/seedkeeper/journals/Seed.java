package game.weekend.seedkeeper.journals;

import java.util.Arrays;

import game.weekend.seedkeeper.db.Error;
import game.weekend.seedkeeper.db.Record;
import game.weekend.seedkeeper.general.Loc;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Seed extends Record {
	public static int NAME_LENGTH = 255;
	public static int ARTICLE_LENGTH = 16;
	public static int DESCRIPTION_LENGTH = 4096;
	public static int VEGETATION_LENGTH = 32;
	public static int MASS_LENGTH = 32;
	public static int HEIGHT_LENGTH = 32;
	public static int YIELD_LENGTH = 32;
	public static int LENGTH_LENGTH = 32;
	public static int SOWING_TIME_LENGTH = 32;
	public static int TRANSPLANT_TIME_LENGTH = 32;
	public static int IN_GROUND_LENGTH = 32;
	public static int PLANTING_SCHEME_LENGTH = 32;
	public static int GROUND_LENGTH = 3;

	private Integer id;
	private Integer brand_id;
	private Integer category_id;
	private Integer status_id;
	private Integer color_id;
	private Integer kind_id;
	private final BooleanProperty mark = new SimpleBooleanProperty(false);
	private final StringProperty name = new SimpleStringProperty("");
	private final StringProperty article = new SimpleStringProperty("");
	private final BooleanProperty hybrid = new SimpleBooleanProperty(false);
	private final IntegerProperty use_by = new SimpleIntegerProperty(0);
	private String description;
	private byte[] photo;
	private final StringProperty vegetation = new SimpleStringProperty("");
	private String mass;
	private String height;
	private String yield;
	private String length;
	private String sowing_time;
	private String transplant_time;
	private String in_ground;
	private String planting_scheme;
	private String ground;

	public Seed() {
	}

	public Seed(Integer id, Boolean mark, String name, String article, Integer use_by, String brand, String vegetation,
			String status, Boolean hybrid, String status_color, Integer status_id, Integer brand_id) {
		this.setId(id);
		this.setBrand_id(brand_id);
		this.setMark(mark);
		this.setName(name);
		this.setArticle(article);
		this.setUse_by(use_by);
		this.setVegetation(vegetation);
		this.setHybrid(hybrid);
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getBrand_id() {
		return brand_id;
	}

	public void setBrand_id(Integer brand_id) {
		this.brand_id = getNormInt(brand_id);
	}

	public Integer getCategory_id() {
		return category_id;
	}

	public void setCategory_id(Integer category_id) {
		this.category_id = getNormInt(category_id);
	}

	public Integer getStatus_id() {
		return status_id;
	}

	public void setStatus_id(Integer status_id) {
		this.status_id = getNormInt(status_id);
	}

	public Integer getColor_id() {
		return color_id;
	}

	public void setColor_id(Integer color_id) {
		this.color_id = getNormInt(color_id);
	}

	public Integer getKind_id() {
		return kind_id;
	}

	public void setKind_id(Integer kind_id) {
		this.kind_id = getNormInt(kind_id);
	}

	public BooleanProperty markProperty() {
		return mark;
	}

	public Boolean getMark() {
		return mark.get();
	}

	public void setMark(Boolean mark) {
		this.mark.setValue(mark);
	}

	public StringProperty nameProperty() {
		return this.name;
	}

	public String getName() {
		return (this.name.get() == null) ? "" : this.name.get();
	}

	public void setName(String name) {
		this.name.set(getNormString(name, NAME_LENGTH));
	}

	public StringProperty articleProperty() {
		return this.article;
	}

	public String getArticle() {
		return (this.article.get() == null) ? "" : this.article.get();
	}

	public void setArticle(String article) {
		this.article.set(getNormString(article, ARTICLE_LENGTH));
	}

	public BooleanProperty hybridProperty() {
		return hybrid;
	}

	public Boolean getHybrid() {
		return hybrid.get();
	}

	public void setHybrid(Boolean hybrid) {
		this.hybrid.setValue(hybrid);
	}

	public IntegerProperty use_byProperty() {
		return this.use_by.get() == 0 ? null : this.use_by;
	}

	public Integer getUse_by() {
		Integer retVal = this.use_by.getValue();
		if (retVal == 0) {
			retVal = null;
		}
		return retVal;
	}

	public void setUse_by(Integer use_by) {
		this.use_by.setValue(use_by);
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = getNormString(description, DESCRIPTION_LENGTH);
	}

	public byte[] getPhoto() {
		return this.photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	public StringProperty vegetationProperty() {
		return this.vegetation;
	}

	public String getVegetation() {
		return this.vegetation.getValue();
	}

	public void setVegetation(String vegetation) {
		this.vegetation.set(getNormString(vegetation, VEGETATION_LENGTH));
	}

	public String getMass() {
		return this.mass;
	}

	public void setMass(String mass) {
		this.mass = getNormString(mass, MASS_LENGTH);
	}

	public String getHeight() {
		return this.height;
	}

	public void setHeight(String height) {
		this.height = getNormString(height, HEIGHT_LENGTH);
	}

	public String getYield() {
		return this.yield;
	}

	public void setYield(String yield) {
		this.yield = getNormString(yield, YIELD_LENGTH);
	}

	public String getLength() {
		return this.length;
	}

	public void setLength(String length) {
		this.length = getNormString(length, LENGTH_LENGTH);
	}

	public String getSowing_time() {
		return this.sowing_time;
	}

	public void setSowing_time(String sowing_time) {
		this.sowing_time = getNormString(sowing_time, SOWING_TIME_LENGTH);
	}

	public String getTransplant_time() {
		return this.transplant_time;
	}

	public void setTransplant_time(String transplant_time) {
		this.transplant_time = getNormString(transplant_time, TRANSPLANT_TIME_LENGTH);
	}

	public String getIn_ground() {
		return this.in_ground;
	}

	public void setIn_ground(String in_ground) {
		this.in_ground = getNormString(in_ground, IN_GROUND_LENGTH);
	}

	public String getPlanting_scheme() {
		return this.planting_scheme;
	}

	public void setPlanting_scheme(String planting_scheme) {
		this.planting_scheme = getNormString(planting_scheme, PLANTING_SCHEME_LENGTH);
	}

	public String getGround() {
		return this.ground;
	}

	public void setGround(String ground) {
		this.ground = getNormString(ground, GROUND_LENGTH);
	}

	public Error check() {
		if (getName() == null || getName().trim().length() == 0)
			return new Error(Loc.get("enter_the_seed_name") + ".", 2);

		if (getArticle() == null || getArticle().trim().length() == 0)
			return new Error(Loc.get("enter_the_article") + ".", 4);

//		if (getCategory_id() == 0)
//			return new Error(Loc.get("enter_the_category") + ".", 2);
//
//		if (getUse_by() != null && getUse_by() < 1000)
//			return new Error(Loc.get("enter_4_digits_in_the_year_field") + ".", 5);

		return null;
	}

	@Override
	public boolean hasDifference(Object o) {
		StringBuilder sb = new StringBuilder();
		setDifferences("");

		if (this == o)
			return false;

		Seed other = (Seed) o;
		boolean result = false;

		result |= checkDifference(Loc.get("of_the_brand_is"), getBrand_id(), other.getBrand_id(), sb);
		result |= checkDifference(Loc.get("of_the_category_is"), getCategory_id(), other.getCategory_id(), sb);
		result |= checkDifference(Loc.get("of_the_status_is"), getStatus_id(), other.getStatus_id(), sb);
		result |= checkDifference(Loc.get("of_the_color_is"), getColor_id(), other.getColor_id(), sb);
		result |= checkDifference(Loc.get("of_the_kind_is"), getKind_id(), other.getKind_id(), sb);

		result |= checkDifference(Loc.get("of_the_name_is"), getName(), other.getName(), sb);
		result |= checkDifference(Loc.get("of_the_article_is"), getArticle(), other.getArticle(), sb);

		result |= checkDifference(Loc.get("of_the_hybrid_is"), getHybrid(), other.getHybrid(), sb);
		result |= checkDifference(Loc.get("of_the_year_is"), getUse_by(), other.getUse_by(), sb);

		result |= checkDifference(Loc.get("of_the_vegetation_is"), getVegetation(), other.getVegetation(), sb);
		result |= checkDifference(Loc.get("of_the_mass_is"), getMass(), other.getMass(), sb);
		result |= checkDifference(Loc.get("of_the_height_is"), getHeight(), other.getHeight(), sb);

		result |= checkDifference(Loc.get("of_the_yield_is"), getYield(), other.getYield(), sb);
		result |= checkDifference(Loc.get("of_the_length_is"), getLength(), other.getLength(), sb);
		result |= checkDifference(Loc.get("of_the_sowing_time_is"), getSowing_time(), other.getSowing_time(), sb);

		result |= checkDifference(Loc.get("of_the_transplant_time_is"), getTransplant_time(),
				other.getTransplant_time(), sb);
		result |= checkDifference(Loc.get("of_the_in_ground_is"), getIn_ground(), other.getIn_ground(), sb);
		result |= checkDifference(Loc.get("of_the_planting_scheme_is"), getPlanting_scheme(),
				other.getPlanting_scheme(), sb);
		result |= checkDifference(Loc.get("of_the_ground_is"), getGround(), other.getGround(), sb);

		byte[] oldPhoto = getPhoto();
		byte[] newPhoto = other.getPhoto();
		if (!Arrays.equals(oldPhoto, newPhoto)) {
			sb.append(Loc.get("there_is_a_difference_in_the_photo") + ".\n");
			result = true;
		}

		String oldDescr = getDescription();
		if (oldDescr == null)
			oldDescr = "";
		String newDescr = other.getDescription();
		if (newDescr == null)
			newDescr = "";
		if (!oldDescr.equals(newDescr)) {
			sb.append(Loc.get("there_is_a_difference_in_the_description") + ".\n");
			result = true;
		}

		if (result)
			setDifferences(sb.toString());

		return result;
	}
}
