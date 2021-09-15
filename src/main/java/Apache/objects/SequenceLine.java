package Apache.objects;

public class SequenceLine extends Line {

    private boolean voided;

    public SequenceLine(
            int indexKey,
            String transCode,
            int qty,
            String mfg,
            String partNumber,
            String description,
            double listPrice,
            double unitPrice,
            String tx
    ) {
        super(indexKey,
                transCode,
                qty,
                mfg,
                partNumber,
                description,
                listPrice,
                unitPrice,
                tx
        );
    }

    public boolean isVoided() {
        return voided;
    }

    public void voidSale() {
        voided = true;
    }

}
