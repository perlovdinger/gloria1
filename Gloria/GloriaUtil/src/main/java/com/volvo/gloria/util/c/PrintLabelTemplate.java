package com.volvo.gloria.util.c;

/**
 * Barcode templates.
 * 
 */
public final class PrintLabelTemplate {
    
    public static final int LBL_MAX_PROJECT_LENGTH = 20;
    public static final int LBL_MAX_TESTOBJECT_LENGTH = 20;
    public static final int LBL_MAX_DELIVERYADDRESS_LENGTH = 255;
    public static final int LBL_MAX_PARTMOD_LENGTH = 60;
    public static final int LBL_MAX_PARTVER_LENGTH = 10;
    public static final int LBL_MAX_PARTNO_LENGTH = 20;
    public static final int LBL_MAX_PARTNAME_LENGTH = 40;
    public static final int MAX2D_LABEL_DATA_LENGTH = 135;
    
    /**
     * ^XA                                           START FORMAT.
     * ^MMT                                          PRINT MODE : 'T' tearoff 
     * ^PW                                           PRINT WIDTH : 809
     * ^LL                                           LABEL LENGTH : 'y-axis' position 0, 406 length
     * ^LS                                           LABEL SHIFT :   0 (print position is less than 0)
     * ^FB                                           FIELD BLOCK 400 pixels 20 lines Justified
     * ^BY10,0                                       BAR CODE FIELD DEFAULT : w=10,r=0
     * ^FT270,330                                    FIELD TYPESET : x-axis pos=270, y-axis pos=330
     * ^BON,6,N,23,N,1                               AZTEC BAR CODE :orientation - N,magnification factor - 10,extended channel interpretation code indicator- N
     *                                                                error control - 23%, menu symbol indicator - N,number of symbols for structured append - 1
     * ^FH^FD%1s                                     FIELD DATA : %5s - bar code data
     * ^FS                                           FIELD SEPARATOR : command denotes the end of the field definition
     * ^FT550,420                                    FIELD TYPESET : x-axis pos=550, y-axis pos=420    
     * ^FB                                           FIELD BLOCK 230 pixels 12 lines Justified      
     * ^A0N,20,22^FH^FD%2s^FS                        FONT SELECTION(A0N) : for fields, %2s - part modification field data
     * ^FT550,176                                    FIELD TYPESET : x-axis pos=550, y-axis pos=176
     * ^A0N,16,20^FH^FDModification:^FS^FT550,491    FONT SELECTION(A0N) : for fields, part modification field label
     * ^FB250,12                                     FIELD BLOCK 250 pixels 12 lines Justified
     * ^A0N,36,36^FH^FD%3s^FS^FT550,61               FONT SELECTION(A0N) : for fields, %3s - part version field data
     * ^A0N,18,21^FH^FDVersion:^FS^FT10,491          FONT SELECTION(A0N) : for fields, part version field label
     * ^FB                                           FIELD BLOCK 250 pixels 12 lines Justified
     * ^A0N,36,36^FH^FD%4s^FS^FT10,61                FONT SELECTION(A0N) : for fields, %4s - part number field data
     * ^A0N,18,21^FH^FDPart No:^FS^FS^FT10,420       FONT SELECTION(A0N) : for fields, part number field label
     * ^FB                                           FIELD BLOCK 250 pixels 12 lines Justified     
     * ^A0N,20,22^FH^FD%5s^FS^FT10,176                FONT SELECTION(A0N) : for fields, %5s - part name field data
     * ^A0N,16,20^FH^FDName:^FS                      FONT SELECTION(A0N) : for fields, part name field label
     * ^PQ%6s,0,1,Y                                  PRINT QUANTITY : %6s - copies field data
     * ^XZ                                           END FORMAT.
     */
    public static final String ZPL_2D_LABEL_TEMPLATE_PART = "^XA^MMT^PW809^LL0406^LS0^FB400,20,0,J^^BY10,0^FT270,330^BON,6,N,23,N,1,^FH^FD%1s^FS"
            + "^FT550,420^FB230,12,0,J^A0N,20,22^FH^FD%2s^FS^FT550,176^A0N,16,20^FH^FDModification:^FS^FT550,491^FB250,12,0,J^A0N,36,36^FH^FD%3s^FS"
            + "^FT550,61^A0N,18,21^FH^FDVersion:^FS^FT10,491^FB250,12,0,J^A0N,36,36^FH^FD%4s^FS^FT10,61^A0N,18,21^FH^FDPart No:^FS^FS"
            + "^FT10,420^FB250,12,0,J^A0N,20,22^FH^FD%5s^FS^FT10,176^A0N,16,20^FH^FDName:^FS^PQ%6s,0,1,Y^XZ";
    
    
    /**
     * ^XA                                           START FORMAT.
     * ^MMT                                          PRINT MODE : 'T' tearoff 
     * ^PW                                           PRINT WIDTH : 809
     * ^LL                                           LABEL LENGTH : 'y-axis' position 0, 406 length
     * ^LS                                           LABEL SHIFT :   0 (print position is less than 0)
     * ^FB                                           FIELD BLOCK 400 pixels 20 lines Justified
     * ^BY10,0                                       BAR CODE FIELD DEFAULT : w=10,r=0
     * ^FT300,280                                    FIELD TYPESET : x-axis pos=300, y-axis pos=280
     * ^BQ,N,2,4,M,7                                 QR BAR CODE :orientation - N,model-2,magnification factor - 2,
     *                                                                error control - M STANDARD LEVEL, mask value - 7
     * ^FDHMA%1s                                     FIELD DATA : %1s - bar code data, H = error correction level (ultra-high reliability level),
     *                                               M- input mode manual input, A- alphanumeric character mode                                               
     * ^FS                                           FIELD SEPARATOR : command denotes the end of the field definition
     * ^FT560,340                                    FIELD TYPESET : x-axis pos=560, y-axis pos=340    
     * ^FB                                           FIELD BLOCK 250 pixels 12 lines Justified      
     * ^A0N,20,22^FH^FD%2s^FS                        FONT SELECTION(A0N) : for fields, %2s - part modification field data
     * ^FT560,96                                     FIELD TYPESET : x-axis pos=560, y-axis pos=96
     * ^A0N,16,20^FH^FDModification:^FS^FT560,332    FONT SELECTION(A0N) : for fields, part modification field label
     * ^FB250,12                                     FIELD BLOCK 250 pixels 12 lines Justified
     * ^A0N,24,24^FH^FD%3s^FS^FT560,43               FONT SELECTION(A0N) : for fields, %3s - part version field data
     * ^A0N,18,21^FH^FDVersion:^FS^FT10,332          FONT SELECTION(A0N) : for fields, part version field label
     * ^FB                                           FIELD BLOCK 250 pixels 12 lines Justified
     * ^A0N,24,24^FH^FD%4s^FS^FT10,43                FONT SELECTION(A0N) : for fields, %4s - part number field data
     * ^A0N,18,21^FH^FDPart No:^FS^FS^FT10,340       FONT SELECTION(A0N) : for fields, part number field label
     * ^FB                                           FIELD BLOCK 250 pixels 12 lines Justified     
     * ^A0N,20,22^FH^FD%5s^FS^FT10,96                FONT SELECTION(A0N) : for fields, %5s - part name field data
     * ^A0N,16,20^FH^FDName:^FS                      FONT SELECTION(A0N) : for fields, part name field label
     * ^PQ%6s,0,1,Y                                  PRINT QUANTITY : %6s - copies field data
     * ^XZ                                           END FORMAT.
     */
    public static final String ZPL_2D_QR_LABEL_TEMPLATE_PART = "^XA^MMT^PW809^LL0406^LS0^FB400,20,0,J^FT300,280^BON,6,N,23,N,1,^FH"
            + "^BQ,N,2,4,M,7^FDHMA%1s^FS^FT560,340^FB250,12,0,J^A0N,20,22^FH^FD%2s^FS^FT560,96^A0N,16,20^FH^FDModification:^FS"
            + "^FT560,332^FB250,12,0,J^A0N,24,24^FH^FD%3s^FS^FT560,43^A0N,18,21^FH^FDVersion:^FS^FT10,332^FB250,12,0,J^A0N,24,24^FH^FD%4s"
            + "^FS^FT10,43^A0N,18,21^FH^FDPart No:^FS^FS^FT10,340^FB250,12,0,J^A0N,20,22^FH^FD%5s^FS^FT10,96^A0N,16,20^FH^FDName:^FS^PQ%6s,0,1,Y^XZ";
    
    /**
     * ^XA                                           START FORMAT.
     * ^MMT                                          PRINT MODE : 'T' tearoff 
     * ^PW                                           PRINT WIDTH : 809
     * ^LL                                           LABEL LENGTH : 'y-axis' position 0, 106 length
     * ^LS                                           LABEL SHIFT :   0 (print position is less than 0)
     * ^BY4,2,160                                    BAR CODE FIELD DEFAULT : w=4,r=2,h=160
     * ^FT60,230                                     FIELD TYPESET : x-axis pos=60, y-axis pos=230
     * ^B3N,N,110,Y,Y                                B3 : 39 BAR CODE, orientation - N(Normal), check digit - N(No), barcode height - 110,
     *                                               print interpretation - Y(Yes),print interpretation line above code - Y(Yes) 
     * ^FD%1s^FS                                     FIELD DATA : %1s - bar code data   
     * ^PQ%2s,0,1,Y                                  PRINT QUANTITY : %2s - copies field data 
     * ^XZ                                           END FORMAT.                                            
     */
    public static final String ZPL_LABEL_TEMPLATE_BINLOCATION_CODE = "^XA^MMT^PW809^LL0106^LS0^BY4,2,160^FT60,230^B3N,N,110,Y,Y^FD%1s^FS^PQ%2s,0,1,Y^XZ";
        
    /**
     * ^XA                                           START FORMAT.
     * ^MMT                                          PRINT MODE : 'T' tearoff 
     * ^PW                                           PRINT WIDTH : 809
     * ^LL                                           LABEL LENGTH : 'y-axis' position 0, 106 length
     * ^LS                                           LABEL SHIFT :   0 (print position is less than 0)
     * ^BY3,2,160                                    BAR CODE FIELD DEFAULT : w=3,r=2,h=160
     * ^FT100,230                                     FIELD TYPESET : x-axis pos=100, y-axis pos=230
     * ^B3N,N,110,Y,Y                                B3 : 39 BAR CODE, orientation - N(Normal), check digit - N(No), barcode height - 110,
     *                                               print interpretation - Y(Yes),print interpretation line above code - Y(Yes) 
     * ^FD%1s^FS                                     FIELD DATA : %1s - bar code data   
     * ^PQ%2s,0,1,Y                                  PRINT QUANTITY : %2s - copies field data 
     * ^XZ                                           END FORMAT.                                            
     */
    public static final String ZPL_LABEL_TEMPLATE_TRANSPORT_LABEL = "^XA^MMT^PW809^LL0106^LS0^BY3,2,160^FT100,230^B3N,N,110,Y,Y^FD%1s^FS^PQ%2s,0,1,Y^XZ";
    
    
    /**
     * ^XA                                           START FORMAT.
     * ^MMT                                          PRINT MODE : 'T' tearoff 
     * ^PW                                           PRINT WIDTH : 809
     * ^LL                                           LABEL LENGTH : 'y-axis' position 0, 406 length
     * ^LS                                           LABEL SHIFT :   0 (print position is less than 0)
     * ^FT400,180                                    FIELD TYPESET : x-axis pos=400, y-axis pos=180                 
     * ^A0N,24,26^FH^FDPick List:%1s^FS^FT8,223      FONT SELECTION(A0N) : for fields, %1s Pick List code
     * ^A0N,24,26^FH^FD^FT170,330^FB600,8,0,J        
       ^A0N,18,20^FH^FD%2s^FS^FT10,215               FONT SELECTION(A0N) : for fields, %2s Delivery address field data
     * ^A0N,24,26^FH^FDDelivery Address:^FSFT8,180   FONT SELECTION(A0N) : for fields, Delivery address field label
     * ^A0N,24,26^FH^FDTest Object: %3s^FS^FT400,140 FONT SELECTION(A0N) : for fields, %3s Test object
     * ^A0N,24,26^FH^FDQty:%5s^FS^FT8,140            FONT SELECTION(A0N) : for fields, %4s Qty
     * ^A0N,24,26^FH^FDProject:%4s^FS"
       ^FT8,121^FT530,114^FB260,3,0,J                FONT SELECTION(A0N) : for fields, %5s Project
     * ^A0N,24,26^FH^FD%6s^FS^FT400,87               FONT SELECTION(A0N) : for fields, %6s Part Modification
     * ^A0N,24,26^FH^FDModification:^FS
       ^FT8,87^FT70,100^FB260,2,0,J
       ^A0N,24,26^FH^FD%7s^FS^FT8,87                 FONT SELECTION(A0N) : for fields, %7s Part Name
     * ^A0N,24,26^FH^FDName:^FS"
            ^FT400,514                               FONT SELECTION(A0N) : for fields label Part Name
     * ^A0N,24,26^FH^FDVersion:%8s^FS^FT7,51         FONT SELECTION(A0N) : for fields, %8s Part Version
     * ^A0N,24,26^FH^FDPart No:%9s^FS                FONT SELECTION(A0N) : for fields, %9s Part Number
     * ^PQ%10s,0,1,Y                                 PRINT QUANTITY : %10s - copies field data                           
     * ^XZ                                           END FORMAT.   
     */
    public static final String ZPL_LABEL_TEMPLATE_PULL_LABEL = "^XA^MMT^PW809^LL0406^LS0^FT400,200^A0N,24,26^FH^FDPick List:%1s"
            + "^FS^FT8,223^A0N,44,26^FH^FD^FT210,320^FB580,6,0,J^A0N,18,20^FH^FD%2s"
            + "^FS^FT10,230^A0N,24,26^FH^FDDelivery Address:^FS^FT8,200^A0N,24,26^FH^FDTest Obj:%3s"
            + "^FS^FT400,166^A0N,24,26^FH^FDQty:%4s^FS^FT8,166^A0N,24,26^FH^FDProject:%5s"
            + "^FS^FT8,121^FT538,160^FB260,4,0,J^A0N,24,26^FH^FD%6s^FS^FT400,87^A0N,24,26^FH^FDModification:^FS^FT8,87^FT76,112^FB250,2,0,J^A0N,24,26^FH^FD%7s"
            + "^FS^FT8,87^A0N,24,26^FH^FDName:^FS^FT400,48,^A0N,24,26^FH^FDVersion:%8s^FS^FT7,48^A0N,24,26^FH^FDPart No:%9s"
            + "^FS^PQ%10s,0,1,Y^XZ";
    
    private PrintLabelTemplate() {
        super();
    }
    
}
