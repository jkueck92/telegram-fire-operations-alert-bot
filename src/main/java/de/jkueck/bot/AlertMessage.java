package de.jkueck.bot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Log4j2
@ToString
@NoArgsConstructor
public class AlertMessage {

    private String city;

    private String address;

    private String keyword;

    private String message;

    private Date alertTimestamp;

    public AlertMessage(String message) {

        if (message != null) {
            Document document = Jsoup.parse(message);

            Element table = document.select("table").get(0);
            Elements rows = table.select("tr");

            Element detailsTable = rows.get(3);
            Elements detailsTableRows = detailsTable.select("tr");

            Element x = detailsTableRows.get(1);

            Node y = x.childNode(1);

            Node detailsMessage = y.childNode(11);
            Node detailsTimestamp = y.childNode(13);

            String[] detailsArray = StringUtils.split(detailsMessage.toString(), "/");

            String[] locationDetailArray = StringUtils.split(detailsArray[0], " ", 2);
            this.city = locationDetailArray[0];
            this.address = locationDetailArray[1];

            this.keyword = detailsArray[2] + detailsArray[3];

            this.message = detailsArray[4];

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            try {
                this.alertTimestamp = simpleDateFormat.parse(detailsTimestamp.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

    }

}
