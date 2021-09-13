package Apache.objects;

public class Part implements Transferable {

    private final String lineCode;
    private final String partNumber;
    private final String description;
    private final double cost;
    private final int stockQuantity;
    private final int availableQuantity;

    public Part(
            String lineCode,
            String partNumber,
            String description,
            double cost,
            int stockQuantity,
            int availableQuantity
    ){
        this.lineCode = lineCode;
        this.partNumber = partNumber;
        this.description = description;
        this.cost = cost;
        this.stockQuantity = stockQuantity;
        this.availableQuantity = availableQuantity;
    }

    public String getLineCode() {
        return lineCode;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public String getDescription() {
        return description;
    }

    public double getCost() {
        return cost;
    }

    public int getStockQuantity(){
        return stockQuantity;
    }

    public int getAvailableQuantity(){
        return availableQuantity;
    }


    @Override
    public String getSelectableName() {
        return lineCode + " " + partNumber + "\t" + description;
    }
}
