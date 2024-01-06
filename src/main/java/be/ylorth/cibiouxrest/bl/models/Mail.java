package be.ylorth.cibiouxrest.bl.models;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Data
@Builder
public class Mail {

    @Data
    public static class HtmlTemplate {
        private String template;
        private Map<String, Object> props;

        public HtmlTemplate(String template, Map<String, Object> props) {
            this.template = template;
            this.props = props;
        }
    }

    private String from;
    private String to;
    private HtmlTemplate htmlTemplate;
    private String subject;
}
