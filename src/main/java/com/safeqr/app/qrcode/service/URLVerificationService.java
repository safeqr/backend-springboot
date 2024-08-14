package com.safeqr.app.qrcode.service;

import static com.safeqr.app.constants.CommonConstants.*;
import com.safeqr.app.qrcode.entity.URLEntity;
import com.safeqr.app.qrcode.model.URLModel;
import com.safeqr.app.qrcode.repository.URLRepository;
import com.safeqr.app.prediction.service.PredictionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class URLVerificationService {
    private static final int CONNECTION_TIMEOUT_MS = 10000;
    private static final int READ_TIMEOUT_MS = 10000;
    private static final Logger logger = LoggerFactory.getLogger(URLVerificationService.class);
    private final URLRepository urlRepository;
    private final PredictionService predictionService;
    @Autowired
    public URLVerificationService(URLRepository urlRepository, PredictionService predictionService) {
        this.urlRepository = urlRepository;
        this.predictionService = predictionService;
    }

    // Regular expression pattern for shortening services
    private static final String SHORTENING_PATTERN =
            "bit\\.ly|goo\\.gl|shorte\\.st|go2l\\.ink|x\\.co|ow\\.ly|t\\.co|tinyurl|tr\\.im|is\\.gd|cli\\.gs|" +
                    "yfrog\\.com|migre\\.me|ff\\.im|tiny\\.cc|url4\\.eu|twit\\.ac|su\\.pr|twurl\\.nl|snipurl\\.com|" +
                    "short\\.to|BudURL\\.com|ping\\.fm|post\\.ly|Just\\.as|bkite\\.com|snipr\\.com|fic\\.kr|loopt\\.us|" +
                    "doiop\\.com|short\\.ie|kl\\.am|wp\\.me|rubyurl\\.com|om\\.ly|to\\.ly|bit\\.do|t\\.co|lnkd\\.in|" +
                    "db\\.tt|qr\\.ae|adf\\.ly|goo\\.gl|bitly\\.com|cur\\.lv|tinyurl\\.com|ow\\.ly|bit\\.ly|ity\\.im|" +
                    "q\\.gs|is\\.gd|po\\.st|bc\\.vc|twitthis\\.com|u\\.to|j\\.mp|buzurl\\.com|cutt\\.us|u\\.bb|yourls\\.org|" +
                    "x\\.co|prettylinkpro\\.com|scrnch\\.me|filoops\\.info|vzturl\\.com|qr\\.net|1url\\.com|tweez\\.me|v\\.gd|" +
                    "tr\\.im|link\\.zip\\.net";

    // Regular expression pattern to match various IP address formats
    private static final String IP_PATTERN =
            "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\/)|" +
                    "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\/)|" +
                    "((0x[0-9a-fA-F]{1,2})\\.(0x[0-9a-fA-F]{1,2})\\.(0x[0-9a-fA-F]{1,2})\\.(0x[0-9a-fA-F]{1,2})\\/)" +
                    "(?:[a-fA-F0-9]{1,4}:){7}[a-fA-F0-9]{1,4}|" +
                    "([0-9]+(?:\\.[0-9]+){3}:[0-9]+)|" +
                    "((?:(?:\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.){3}(?:25[0-5]|2[0-4]\\d|[01]?\\d\\d|\\d)(?:\\/\\d{1,2})?)";

    // Define a Set of suspicious file extensions
    private static final Set<String> SUSPICIOUS_EXTENSIONS = Stream.of(
            ".exe", ".bat", ".sh", ".cmd", ".scr", ".pif", ".application", ".gadget",
            ".vb", ".vbs", ".js", ".jse", ".ws", ".wsf", ".msc", ".cpl",
            ".msi", ".ps1", ".py", ".pyc", ".pyo", ".rb", ".bin", ".run"
    ).collect(Collectors.toUnmodifiableSet());


    // Checks if the URL has executable file
    public String hasExecutableFile(String urlPath) {
        return Stream.of(urlPath)
                .map(String::toLowerCase)
                .map(path -> {
                    int lastDotIndex = path.lastIndexOf('.');
                    if (lastDotIndex != -1) {
                        return path.substring(lastDotIndex);
                    }
                    return path.contains(".") || path.endsWith("/") ? null : "";
                })
                .filter(Objects::nonNull)
                .map(extension -> SUSPICIOUS_EXTENSIONS.contains(extension) || extension.isEmpty() ? "Yes" : "")
                .findFirst()
                .orElse("");
    }

    public URLEntity getURLEntityByQRCodeId(UUID qrCodeId) {
        logger.info("qrCodeId retrieving: {}", qrCodeId);
//        return urlRepository.findByQrCodeId(qrCodeId)
//                .orElseThrow(() -> new ResourceNotFoundExceptions("URL not found for QR Code id: " + qrCodeId));
        return urlRepository.findByQrCodeId(qrCodeId).orElse(null);
    }

    public void insertDB(URLEntity urlEntity) {
        urlRepository.save(urlEntity);
    }
    // Function to breakdown URL into subdomain, domain, topLevelDomain, query params, fragment
    public URLEntity breakdownURL(String urlString) {
        URLEntity urlObj = new URLEntity();
        try {
            //URL url = new URI(encodeUrl(urlString)).toURL();
            URL url = new URI(urlString.replace(" ", "")).toURL();
            // Check for URL encoding in path and query
            String query = parseQueryParams(url.getQuery());
            String pathEncoding = checkURLEncoding(url.getPath());
            String queryEncoding = query != null ? checkURLEncoding(query) : "";

            // Combine encoding results
            urlObj.setUrlEncoding(pathEncoding.equals("Yes") || queryEncoding.equals("Yes") ? "Yes" : "");

            // encode url before proceeding the rest of the checks
            url = new URI(encodeUrl(urlString)).toURL();
            String host = url.getHost();
            populateHostDetails(host, urlObj);

            // Check for deceptive URL
            urlObj.setHostnameEmbedding(checkDeceptiveUrl(url));

            // Check for Javascript code in url
            urlObj.setJavascriptCheck(checkForJavascriptCode(urlString));

            // Check for url shortener
            urlObj.setShorteningService(hasShorteningService(urlString));

            // Check for IP address
            urlObj.setHasIpAddress(hasIPAddress(urlString));

            // Check for suspicious file extensions
            urlObj.setHasExecutable(hasExecutableFile(urlString));

            urlObj.setPath(Optional.ofNullable(url.getPath()).filter(p -> !p.isEmpty()).orElse(""));

            urlObj.setQuery(parseQueryParams(url.getQuery()));
            urlObj.setFragment(Optional.ofNullable(url.getRef()).orElse(""));

            // Check for tracking parameters
            urlObj.setTrackingDescriptions(getTrackingDescriptions(url.getQuery()));

        } catch (Exception e) {
            logger.error("Error in breaking down URL: {}", e.getMessage());
            e.printStackTrace();
        }
        return urlObj;
    }

    private void populateHostDetails(String host, URLEntity urlObj) {
        logger.info("Host: {}", host);

        if (host != null && !host.isEmpty()) {
            if (isIpAddress(host)) {
                // Handle IP address
                urlObj.setDomain(host);
                urlObj.setTopLevelDomain("");  // No TLD for IP addresses
                urlObj.setSubdomain("");       // No subdomain for IP addresses
            } else {
                // Handle regular domain name
                String[] hostParts = host.split("\\.");

                int length = hostParts.length;

                if (length >= 2) {
                    urlObj.setTopLevelDomain(hostParts[length - 1]); // TLD, e.g., "com"
                    urlObj.setDomain(hostParts[length - 2]);         // Domain, e.g., "example"
                    urlObj.setSubdomain(length > 2 ? String.join(".", Arrays.copyOfRange(hostParts, 0, length - 2)) : "");
                } else if (length == 1) {
                    // Handle cases like 'localhost' where there's no TLD
                    urlObj.setDomain(hostParts[0]);
                    urlObj.setTopLevelDomain("");  // No TLD
                    urlObj.setSubdomain("");       // No subdomain
                }
            }
        }
    }
    // List of common tracking parameters with their descriptions
    private static final Map<String, String> TRACKING_DESCRIPTIONS = Map.ofEntries(
            Map.entry("utm_source", "Campaign Source: Identifies which site sent the traffic."),
            Map.entry("utm_medium", "Campaign Medium: Identifies what type of link was used."),
            Map.entry("utm_campaign", "Campaign Name: Identifies a specific product promotion or campaign."),
            Map.entry("utm_term", "Campaign Term: Identifies search terms."),
            Map.entry("utm_content", "Campaign Content: Differentiates similar content or links within the same ad."),
            Map.entry("gclid", "Google Click Identifier: Used by Google Ads to track clicks."),
            Map.entry("fbclid", "Facebook Click Identifier: Used by Facebook to track clicks."),
            Map.entry("tracking_id", "Tracking ID: General identifier for tracking purposes."),
            Map.entry("affiliate_id", "Affiliate ID: Identifies traffic from affiliates."),
            Map.entry("ref", "Referrer: Identifies the referrer site."),
            Map.entry("referrer", "Referrer: Identifies the referrer site.")
    );

    // Regex pattern to capture key-value pairs in the query string
    private static final Pattern PARAM_PATTERN = Pattern.compile(
            "(?<key>[^=&]+)=(?<value>[^&]+)",
            Pattern.CASE_INSENSITIVE
    );

    // Static method to detect and return tracking parameter descriptions in a URL
    private List<String> getTrackingDescriptions(String query) {
        if (query == null || query.isEmpty()) {
            return Collections.emptyList();
        }

        Matcher matcher = PARAM_PATTERN.matcher(query);
        List<String> foundDescriptions = new ArrayList<>();

        while (matcher.find()) {
            String key = matcher.group("key").toLowerCase();
            String value = URLDecoder.decode(matcher.group("value"), StandardCharsets.UTF_8);
            if (TRACKING_DESCRIPTIONS.containsKey(key)) {
                foundDescriptions.add(TRACKING_DESCRIPTIONS.get(key) + ": " + value);
            }
        }

        return foundDescriptions;
    }

    private int checkDeceptiveUrl(URL url) {
        String[] parts = url.getHost().split("\\.");
        if (parts.length < 3) return 0;

        Set<String> commonTlds = new HashSet<>(Arrays.asList("com", "org", "net", "edu", "gov"));

        for (int i = parts.length - 2; i >= 1; i--) {
            if (commonTlds.contains(parts[i]) && !commonTlds.contains(parts[i - 1]) && i != parts.length - 2) {
                logger.warn("Potentially deceptive URL detected: {} (Suspicious domain: {}.{})",
                        url, parts[i - 1], parts[i]);
                return 1;
            }
        }
        return 0;
    }

    private String checkForJavascriptCode(String url) {
        // Decode the URL
        String decodedUrl = URLDecoder.decode(url, StandardCharsets.UTF_8);

        // Patterns to detect 'javascript:', '<script>', and 'on*=' attributes
        List<Pattern> maliciousPatterns = Arrays.asList(
                Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
                Pattern.compile("<\\s*script", Pattern.CASE_INSENSITIVE),
                Pattern.compile("on(click|mouseover|load|error|unload|submit|reset|focus|blur|change|select|keydown|keyup|keypress|mousedown|mousemove|mouseup|mouseenter|mouseleave|contextmenu|dblclick)\\s*=", Pattern.CASE_INSENSITIVE)
        );

        // Check for any malicious pattern in the URL
        for (Pattern pattern : maliciousPatterns) {
            Matcher matcher = pattern.matcher(decodedUrl);
            if (matcher.find()) {
                return "Javascript found in URL.";
            }
        }
        return "";
    }

    // Function to detect if the URL uses a shortening service
    private String hasShorteningService(String url) {
        Pattern pattern = Pattern.compile(SHORTENING_PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url);
        return matcher.find() ? "Yes" : "";
    }

    // Function to check text encoding in a URL
    private static String checkURLEncoding(String pathTextPart) {
        // Decode the text
        String decodedText = URLDecoder.decode(pathTextPart, StandardCharsets.UTF_8);

        // Check if the decoded text matches the original text
        return decodedText.equals(pathTextPart) ? "" : "Yes";
    }

    // Function to detect if the URL has an IP address
    private static String hasIPAddress(String url) {
        Pattern pattern = Pattern.compile(IP_PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url);
        return matcher.find() ? "URL contains IP address." : "";
    }
    // Check if the host is an IP address
    private boolean isIpAddress(String host) {
        // Regex to match IPv4 addresses
        String ipv4Pattern = "\\d+\\.\\d+\\.\\d+\\.\\d+";
        // Regex to match IPv6 addresses
        String ipv6Pattern = "([a-fA-F0-9:]+:+)+[a-fA-F0-9]+";

        return host.matches(ipv4Pattern) || host.matches(ipv6Pattern);
    }
    private String parseQueryParams(String query) {
        if (query == null || query.isEmpty()) return "{}";
        Map<String, String> queryParams = new HashMap<>();
        for (String param : query.split("&")) {
            String[] pair = param.split("=", 2);
            String key = pair[0];
            String value = pair.length > 1 ? pair[1] : "";
            if (!key.isEmpty()) {
                queryParams.put(key, value);
            }
        }
        return queryParams.toString();
    }

    private String encodeUrl(String urlString) throws MalformedURLException {
        try {
            URL url = new URL(urlString);
            String protocol = url.getProtocol();
            String host = url.getHost();
            int port = url.getPort();
            String path = url.getPath();
            String query = url.getQuery();
            String ref = url.getRef();

            StringBuilder encodedUrl = new StringBuilder();
            encodedUrl.append(protocol).append("://").append(host);
            if (port != -1) {
                encodedUrl.append(":").append(port);
            }
            encodedUrl.append(URLEncoder.encode(path, StandardCharsets.UTF_8).replace("%2F", "/"));

            if (query != null) {
                encodedUrl.append("?").append(URLEncoder.encode(query, StandardCharsets.UTF_8).replace("%3D", "=").replace("%26", "&"));
            }
            if (ref != null) {
                encodedUrl.append("#").append(URLEncoder.encode(ref, StandardCharsets.UTF_8));
            }

            return encodedUrl.toString();
        } catch (Exception e) {
            throw new MalformedURLException("Failed to encode URL: " + e.getMessage());
        }
    }

    public void countAndTrackRedirects(String urlString, URLEntity details) throws IOException {
        try {
            URI uri = new URI(encodeUrl(urlString));
            URL url = uri.toURL();
            List<String> redirectChain = new ArrayList<>();
            List<String> hstsHeaderList = new ArrayList<>();
            List<Boolean> sslStrippingList = new ArrayList<>();

            // Add the initial URL to the chain
            redirectChain.add(urlString);
            boolean redirected;
            int redirectCount = 0;

            do {
                URLConnection testConnection = url.openConnection();

                if (!(testConnection instanceof HttpURLConnection)) {
                    // Handle non-HTTP connections (like mailto:)
                    logger.info("Non-HTTP URL encountered: {}", url);
                    hstsHeaderList.add(INFO_HSTS_NOT_APPLICABLE);
                    sslStrippingList.add(false);
                    break;
                }
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setInstanceFollowRedirects(false);
                connection.setConnectTimeout(CONNECTION_TIMEOUT_MS);
                connection.setReadTimeout(READ_TIMEOUT_MS);

                int responseCode = connection.getResponseCode();
                redirected = (responseCode >= 300 && responseCode < 400);

                // Checks for HSTS Header
                hstsHeaderList.add(detectHSTSHeader(url, connection));

                // Handle redirects
                if (redirected) {
                    // Location header contains the URL to redirect to
                    String newUrl = connection.getHeaderField("Location");
                    if (newUrl == null) {
                        break;
                    }
                    URI newUri = uri.resolve(newUrl);
                    // check for SSL stripping during redirect
                    sslStrippingList.add(checkRedirectForSSLStripping(uri, newUri));

                    // Handle relative URLs
                    uri = uri.resolve(newUrl);
                    url = uri.toURL();
                    redirectChain.add(url.toString());
                    redirectCount++;
                    logger.info("Redirect #{}: {}",redirectCount, newUrl);
                } else {
                    // No redirect, so no SSL stripping
                    sslStrippingList.add(false);
                }

                connection.disconnect();
            } while (redirected && redirectCount < MAX_REDIRECT_COUNT);

            details.setRedirect(redirectChain.size() - 1);
            details.setRedirectChain(redirectChain);
            details.setSslStripping(sslStrippingList);
            details.setHstsHeader(hstsHeaderList);
        } catch (URISyntaxException e){
            logger.error("Error in breaking down URL: {}", e.getMessage());
        } catch (SSLHandshakeException e) {
            logger.error("SSL Handshake Exception: {}", e.getMessage());
            details.setSslError("SSL Handshake Exception: " + e.getMessage());
        } catch (SocketTimeoutException e) {
            logger.error("Connection timed out: {}", e.getMessage());
            details.setDnsError("Connection timed out: " + e.getMessage());
        } catch (UnknownHostException e) {
            logger.error("Unknown Host Exception: {}", e.getMessage());
            details.setDnsError("Unknown Host Exception: " + e.getMessage());
        } catch (NoRouteToHostException e) {
            details.setDnsError("Error: " + e.getMessage());
        } catch (ConnectException e) {
            details.setDnsError("Connection Error: " + e.getMessage());
        } catch (SocketException e) {
            details.setDnsError("Socket Error: " + e.getMessage());
        } catch (Exception e) {
            details.setDnsError("Exception: " + e.getMessage());
        }
    }
    // Function to check if the redirect is from HTTPS to HTTP
    private boolean checkRedirectForSSLStripping(URI originalUri, URI newUri) {
        return originalUri.getScheme().equalsIgnoreCase("https") &&
                newUri.getScheme().equalsIgnoreCase("http");
    }
    // Function to check if HSTS header is present for HTTPS connections
    private String detectHSTSHeader(URL url, HttpURLConnection connection) {
        if (connection instanceof HttpsURLConnection) {
            String hstsHeader = connection.getHeaderField("Strict-Transport-Security");
            if (hstsHeader != null && !hstsHeader.isEmpty()) {
                logger.info("HSTS Header detected for {}: {}", url, hstsHeader);
                return INFO_HSTS_HEADER_PREFIX + hstsHeader;
            } else {
                logger.warn("No HSTS Header for HTTPS connection to {}", url);
                return INFO_NO_HSTS_HEADER;
            }
        }
        return INFO_NON_SECURE_CONNECTION;
    }

    // Get Classification using ML Model
    public String getClassification(URLModel urlModel){
        // Call ML model
        String category = predictionService.predict(urlModel);

        //update in category in url table
        urlModel.getDetails().setClassifications(category);

        // return classification results
        if (category.equals(CAT_BENIGN)) {
            if (!urlModel.getDetails().getTrackingDescriptions().isEmpty() || // contains tracking
                urlModel.getData().getInfo().getPrefix().equalsIgnoreCase("http://") || // uses http
                urlModel.getDetails().getSslStripping().contains(true) || // has SSL stripping
                urlModel.getDetails().getHasExecutable().equalsIgnoreCase("yes") || // contains executable
                !urlModel.getDetails().getJavascriptCheck().isEmpty() || // contains javascript
                !urlModel.getDetails().getHasIpAddress().isEmpty() || // contains IP address
                urlModel.getDetails().getHostnameEmbedding() != null // contains hostname embedding

            ) {
                return CLASSIFY_WARNING;
            }
            return CLASSIFY_SAFE;
        }
        return CLASSIFY_UNSAFE;
    }
}