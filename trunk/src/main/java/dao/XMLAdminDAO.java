package dao;

import model.Administrator;
import dao.admin.ObjectFactory;
import dao.admin.XMLAdmin;
import dao.admin.XMLAdminRoot;

public class XMLAdminDAO extends XMLAbstractDAO<XMLAdminRoot> {

	private static XMLAdminDAO instance = null;

	public static XMLAdminDAO getInstance() {
		if (instance == null)
			instance = new XMLAdminDAO();
		return instance;
	}

	private XMLAdminDAO() {
		// Memory filenames in case everything fails
		super("resources/admin.xml", "resources/admin.xsd", "admin.xml", "admin.schema");
		load();
	}

	@Override
	protected XMLAdminRoot createRoot() {
		ObjectFactory objFact = new ObjectFactory();
		return objFact.createXMLAdminRoot();
	}

	public Administrator getAdministrator(String name) {
		for (XMLAdmin admin : rootElement.getAdmin())
			if (admin.getName().equals(name)) {
				Administrator out = new Administrator(admin.getName(),
						admin.getPassword());
				return out;
			}
		return null;
	}

	private XMLAdmin getXMLAdmin(String name) {
		for (XMLAdmin admin : rootElement.getAdmin())
			if (admin.getName().equals(name))
				return admin;
		return null;
	}

	public void saveAdministrator(Administrator admin) {
		XMLAdmin xmlAdmin = getXMLAdmin(admin.getName());

		if (xmlAdmin == null) {
			ObjectFactory objFact = new ObjectFactory();
			xmlAdmin = objFact.createXMLAdmin();
			xmlAdmin.setName(admin.getName());
			rootElement.getAdmin().add(xmlAdmin);
		}

		xmlAdmin.setPassword(admin.getPassword());
	}
}
