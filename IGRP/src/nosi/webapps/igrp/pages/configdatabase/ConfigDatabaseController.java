/*-------------------------*/

/*Create Controller*/

package nosi.webapps.igrp.pages.configdatabase;
/*---- Import your packages here... ----*/
import nosi.core.webapp.Controller;
import nosi.core.webapp.FlashMessage;
import nosi.core.config.Config;
import nosi.core.igrp.mingrations.MigrationIGRP;
import java.io.IOException;
import nosi.core.webapp.Response;
import nosi.core.webapp.helpers.FileHelper;
import nosi.webapps.igrp.dao.Application;
import nosi.webapps.igrp.dao.Config_env;
import nosi.webapps.igrp.pages.migrate.Migrate;
import nosi.core.webapp.Igrp;
import nosi.core.webapp.RParam;

/*---- End ----*/

public class ConfigDatabaseController extends Controller {		


	public Response actionIndex(@RParam(rParamName = "id") String idAplicacao) throws IOException, IllegalArgumentException, IllegalAccessException{
		/*---- Insert your code here... ----*/
		ConfigDatabase model = new ConfigDatabase();
		if(Igrp.getMethod().equalsIgnoreCase("post")){
			model.load();
		}
		if(idAplicacao!=null && !idAplicacao.equals("")){
			ConfigDatabaseView view = new ConfigDatabaseView(model);
			view.aplicacao.setSqlQuery("SELECT id as ID, name as NAME FROM tbl_env WHERE id="+Integer.parseInt(idAplicacao));
			view.tipo_base_dados.setValue(Config.getDatabaseTypes());;
			return this.renderView(view);
		}
		return this.redirect("igrp", "error-page", "exception");
		/*---- End ----*/
	}


	public Response actionGravar() throws IOException, IllegalArgumentException, IllegalAccessException{
		/*---- Insert your code here... ----*/
		ConfigDatabase model = new ConfigDatabase();
		if(Igrp.getMethod().equalsIgnoreCase("post")){
			model.load();
			Config_env config = new Config_env();
			config.setApplication(new Application().findOne(Integer.parseInt(model.getAplicacao())));
			config.setCharset("utf-8");
			config.setHost(model.getHostname());
			config.setName("");
			config.setName_db(model.getNome_de_bade_dados());
			config.setUsername(model.getUsername());
			config.setPassword(model.getPassword());
			config.setPort(model.getPort());
			config.setType_db(model.getTipo_base_dados());
			config.setName(model.getNome_de_conxeao());
			Migrate m = new Migrate();
			m.load();
			if(!MigrationIGRP.validate(m)){
				Igrp.getInstance().getFlashMessage().addMessage(FlashMessage.ERROR, "Falha na Conex�o Com a Base de Dados");
				return this.forward("igrp","ConfigDatabase","index&id="+model.getAplicacao());
			}
			boolean check = new Config_env().find().andWhere("name", "=", config.getName()).one()==null;
			if(check){
				config = config.insert();
				if(config != null){
					this.saveConfigHibernateFile(config);
					Igrp.getInstance().getFlashMessage().addMessage(FlashMessage.SUCCESS,FlashMessage.MESSAGE_SUCCESS);
					Igrp.getInstance().getFlashMessage().addMessage(FlashMessage.INFO,"Nome da conex�o: "+config.getName());
					return this.forward("igrp","ConfigDatabase","index&id="+model.getAplicacao());
				}
			}else{
				Igrp.getInstance().getFlashMessage().addMessage(FlashMessage.WARNING,"Nome de conxe�o j� existe");
				return this.forward("igrp","ConfigDatabase","index&id="+model.getAplicacao());
			}
		}
		return this.redirect("igrp","ConfigDatabase","index");
		/*---- End ----*/
	}
	
	private void saveConfigHibernateFile(Config_env config) throws IOException {
		String package_ = "nosi.webapps."+config.getApplication().getDad().toLowerCase()+".dao";
		String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<!DOCTYPE hibernate-configuration SYSTEM \"http://www.hibernate.org/dtd/hibernate-configuration\">\n"
				+ "<hibernate-configuration>\n"
				+ "		<session-factory>\n"
				+ "				<!-- Mapping your class here... \n"
				+ "					Ex: <mapping class=\""+package_+".Employee\"/>\n"
				+ "				-->\n"
				+ "		</session-factory>\n"
				+ "</hibernate-configuration>";
		String pathWS = Config.getWorkspace()+"/src";
		String pathServer = Config.getBasePathClass();
		FileHelper.save(pathServer, config.getName()+".cfg.xml", content);
		if(FileHelper.fileExists(pathWS)){
			FileHelper.save(pathWS, config.getName()+".cfg.xml", content);
		}
	}


	/*---- Insert your actions here... ----*/
	public Response actionTestar_conexao() throws IOException, IllegalArgumentException, IllegalAccessException{
		/*---- Insert your code here... ----*/
		Migrate model = new Migrate();
		if(Igrp.getMethod().equalsIgnoreCase("post")){
			model.load();
			if(MigrationIGRP.validate(model)){
				Igrp.getInstance().getFlashMessage().addMessage(FlashMessage.SUCCESS, "Conetado com sucesso");
			}else{
				Igrp.getInstance().getFlashMessage().addMessage(FlashMessage.ERROR, "Falha na Conex�o Com a Base de Dados");
			}
		}
		return this.forward("igrp","ConfigDatabase","index&id="+model.getAplicacao());
		/*---- End ----*/
	}
	/*---- End ----*/
}
