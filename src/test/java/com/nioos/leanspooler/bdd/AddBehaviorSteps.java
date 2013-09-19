package com.nioos.leanspooler.bdd;



import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.jbehave.core.annotations.AfterStories;
import org.jbehave.core.annotations.BeforeStories;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.Assert;

import com.nioos.leanspool.dao.DataSourceUtils;



/**
 * Tests steps.
 * @author Hipolito Jimenez.
 */
public class AddBehaviorSteps {
	
	
	/**
	 * Job ID len.
	 */
	private static final int JOB_ID_LEN = 36;
	
	
	/**
	 * The database connection.
	 */
	private transient IDatabaseConnection dbConnection;
	
	
	/**
	 * The process.
	 */
	private transient Process process;
	
	
	/**
	 * The job id.
	 */
	private transient String jobId;
	
	
	/**
	 * Setup before the tests.
	 * Cleanup and fill the database.
	 * @throws Exception on error.
	 */
	@BeforeStories
	public final void beforeStories() throws Exception { // NOPMD
		final DataSource dataSource =
				DataSourceUtils.buildDataSource("/jdbc.properties");
		dbConnection = new DatabaseDataSourceConnection(dataSource);
		final FlatXmlDataSetBuilder flatXmlDataSetBuilder =
			new FlatXmlDataSetBuilder();
		final IDataSet printersDataSet =
			flatXmlDataSetBuilder.build(
				new File(
				"../leanspooldao/src/test/resources/dbunit/PrintersTest.xml"));
		DatabaseOperation.CLEAN_INSERT.execute(dbConnection, printersDataSet);
		final IDataSet printJobsDataSet =
			flatXmlDataSetBuilder.build(
				new File(
			"../leanspooldao/src/test/resources/dbunit/PrintJobsTest.xml"));
		DatabaseOperation.CLEAN_INSERT.execute(dbConnection, printJobsDataSet);
		dbConnection.getConnection().commit();
	}
	
	
	/**
	 * Cleanup after the tests.
	 * Close database connection.
	 * @throws SQLException on error.
	 */
	@AfterStories
	public final void afterStories() throws SQLException {
		dbConnection.close();
	}
	
	
	/**
	 * Step.
	 * Given the user execute the command $cmd.
	 * @param cmd the command.
	 * @throws IOException on error.
	 */
	@Given("the user execute the command $cmd")
	public final void givenTheUserExecuteTheCommand(final String cmd)
			throws IOException {
		process = Runtime.getRuntime().exec(cmd);
	}
	
	
	/**
	 * Step.
	 * When the command returns.
	 * @throws InterruptedException on error.
	 * @throws IOException on error.
	 */
	@When("the command returns")
	public final void whenTheCommandReturns()
			throws InterruptedException, IOException {
		final int res = process.waitFor();
		Assert.assertEquals("Process error", 0, res);
		final InputStream inputStream = process.getInputStream();
		final int size = inputStream.available();
		final byte[] buffer = new byte[size];
		final int readed = inputStream.read(buffer);
		Assert.assertEquals("Invalid number of bytes readed", size, readed);
		jobId = new String(buffer, "UTF-8");
		Assert.assertEquals("Invalid jobId len", JOB_ID_LEN, jobId.length());
	}
	
	
	/**
	 * Step.
	 * Then the database contains the new print job.
	 * @throws SQLException on error.
	 * @throws DataSetException on error.
	 */
	@Then("the database contains the new print job")
	public final void thenTheDatabaseContainsTheNewPrintJob()
			throws DataSetException, SQLException {
		final String sql = "SELECT PrinterName, JobStatus FROM PrintJob "
			+ " WHERE JobId = '" + jobId + "'";
		final ITable queryResult =
			dbConnection.createQueryTable("TEST_RESULT", sql);
		final String printerName =
			(String) queryResult.getValue(0, "PrinterName");
		final String jobStatus = (String) queryResult.getValue(0, "JobStatus");
		//
		Assert.assertEquals("Invalid printerName", "web", printerName);
		Assert.assertEquals("Invalid jobStatus", "New", jobStatus);
	}
	
	
}
