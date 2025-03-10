package org.example.controller;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.model.APIParam;
import org.example.model.ModelType;
import org.example.service.APIService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

public class APIController extends HttpServlet {
    @WebServlet(name = "APIServlet", value = "/api")
    public static class APIServlet extends HttpServlet {
        final APIService apiService = APIService.getInstance();
        final Logger logger = Logger.getLogger(APIController.class.getName());

        @Override
        public void init(ServletConfig config) throws ServletException {
            logger.info("APIController init");
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            String prompt = req.getParameter("prompt");
            String model = req.getParameter("model");
            ModelType modelType = ModelType.valueOf(model);

            PrintWriter out = resp.getWriter();
            APIParam apiParam = new APIParam(prompt, modelType);

            try {
                out.println(apiService.callAPI(apiParam));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
