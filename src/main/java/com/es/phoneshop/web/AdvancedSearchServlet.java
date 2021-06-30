package com.es.phoneshop.web;

import com.es.phoneshop.exception.InvalidValueException;
import com.es.phoneshop.model.product.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

import static com.es.phoneshop.util.Messages.QUANTITY_NON_POSITIVE_VALUE;

public class AdvancedSearchServlet extends HttpServlet {
    private ProductDao productDao;
    private final String INVALID_PRICE_MESSAGE = MessageFormat.format("Value must be a number, > 0 and less than {0}.", Long.MAX_VALUE);

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("products", new ArrayList<Product>());
        request.setAttribute("descriptionSearchTypes", DescriptionSearchTypes.values());
        request.getRequestDispatcher("/WEB-INF/pages/advancedSearch.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String description = request.getParameter("description");
        String minPrice = request.getParameter("minPrice");
        String maxPrice = request.getParameter("maxPrice");
        String descriptionSearchType = request.getParameter("descriptionSearchType");

        Map<String, String> errors = new HashMap();
        Map<SearchFields, String> searchParameters = new HashMap<>();
        searchParameters.put(SearchFields.DESCRIPTION, description);
        Locale locale = request.getLocale();

        if (isNotEmpty(minPrice) && !isValidPriceValue(minPrice, locale)) {
            errors.put("minPrice", INVALID_PRICE_MESSAGE);
        } else {
            searchParameters.put(SearchFields.MIN_PRICE, minPrice);
        }

        if (isNotEmpty(maxPrice) && !isValidPriceValue(maxPrice, locale)) {
            errors.put("maxPrice", INVALID_PRICE_MESSAGE);
        } else {
            searchParameters.put(SearchFields.MAX_PRICE, maxPrice);
        }

        if (!isValidDescriptionType(descriptionSearchType)) {
            errors.put("descriptionSearchType", "Search type must not be empty and be one of the suggested options");
        } else {
            searchParameters.put(SearchFields.DESCRIPTION_TYPE, descriptionSearchType);
        }

        request.setAttribute("description", description);
        request.setAttribute("minPrice", minPrice);
        request.setAttribute("maxPrice", maxPrice);
        request.setAttribute("descriptionSearchTypes", DescriptionSearchTypes.values());
        List<Product> products;
        if (errors.isEmpty()) {
            products = productDao.findProductsAdvanced(searchParameters);
        } else {
            products = new ArrayList<>();
        }

        request.setAttribute("errors", errors);
        request.setAttribute("products", products);
        request.getRequestDispatcher("/WEB-INF/pages/advancedSearch.jsp").forward(request, response);
    }

    private boolean isValidPriceValue(String value, Locale locale) {
        Long quantity;
        try {
            Scanner scanner = new Scanner(value);
            scanner.useLocale(locale);
            quantity = scanner.nextLong();
            if (quantity <= 0) throw new InvalidValueException( QUANTITY_NON_POSITIVE_VALUE );
        } catch (InputMismatchException | InvalidValueException e) {
            return false;
        }
        return true;
    }

    private boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private boolean isValidDescriptionType(String descriptionSearchType) {
        return descriptionSearchType != null && (descriptionSearchType.equals("ALL_WORDS") || descriptionSearchType.equals("ANY_WORD"));
    }
}
