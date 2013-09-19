package com.nioos.leanspooler.servlets;



import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.nioos.leanspool.dao.DaoException;
import com.nioos.leanspool.gwt.shared.PrintJobModel;
import com.nioos.leanspool.printjobs.PrintJobModelImpl;
import com.nioos.leanspool.printjobs.PrintJobsDao;
import com.nioos.leanspool.printjobs.PrintJobsException;



/**
 * AddPrintJobServlet.
 * Add a print job to the spooler system.
 * @author Hipolito Jimenez.
 */
public class AddPrintJobServlet extends HttpServlet {
	
	
	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = -5688828727298372802L;
	
	
	/**
	 * The print jobs DAO.
	 */
	private final transient PrintJobsDao printJobsDao;
	
	
	/**
	 * Constructor.
	 * @throws DaoException on error.
	 */
	public AddPrintJobServlet() throws DaoException {
		super();
		printJobsDao = new PrintJobsDao();
	}
	
	
	@Override
	protected final void doGet(final HttpServletRequest request,
			final HttpServletResponse response)
		throws ServletException, IOException {
		doPost(request, response);
	}
	
	
	@Override
	protected final void doPost(final HttpServletRequest request,
				final HttpServletResponse response)
			throws ServletException, IOException {
		final ServletInputStream servletInputStream = request.getInputStream();
		final PrintJobModel printJobModel = new PrintJobModelImpl(); // NOPMD
		final int size = request.getContentLength();
		if (size > 0) {
			final byte[] buffer = new byte[size];
			IOUtils.readFully(servletInputStream, buffer);
			printJobModel.setJobData(buffer);
		} else {
			final byte[] buffer = IOUtils.toByteArray(servletInputStream);
			printJobModel.setJobData(buffer);
		}
		final String printer = request.getHeader("printer");
		printJobModel.setPrinterName(printer);
		//TODO
		//printJobModel.setJobSize(size);
		//printJobModel.setDate(date);
		//printJobModel.setRemoteIp(remoteId);
		//printJobModel.setUser(user);
		//
		try {
			final String jobId = printJobsDao.insertNewJob(printJobModel);
			response.setContentType("text/plain");
			response.getWriter().print(jobId);
		} catch (DaoException daoExc) {
			sendResponseError(daoExc, response);
		} catch (PrintJobsException pje) {
			sendResponseError(pje, response);
		}
	}
	
	
	/**
	 * Send response error.
	 * @param exception the exception.
	 * @param response the response object.
	 * @throws IOException on error.
	 */
	private void sendResponseError(final Exception exception,
			final HttpServletResponse response) throws IOException {
		log("Cannot add print job", exception);
		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
			exception.getMessage());
	}
	
	
}
