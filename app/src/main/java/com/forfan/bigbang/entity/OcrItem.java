package com.forfan.bigbang.entity;

import java.util.List;

/**
 * Created by wangyan-pd on 2016/11/2.
 */

public class OcrItem {

    /**
     * ParsedResults : [{"TextOverlay":{"Lines":[{"Words":[{"WordText":"Word 1","Left":106,"Top":91,"Height":9,"Width":11},{"WordText":"Word 2","Left":121,"Top":90,"Height":13,"Width":51}],"MaxHeight":13,"MinTop":90}],"HasOverlay":true,"Message":null},"FileParseExitCode":"1","ParsedText":"This is a sample parsed result","ErrorMessage":null,"ErrorDetails":null},{"TextOverlay":null,"FileParseExitCode":-10,"ParsedText":null,"ErrorMessage":"...error message (if any)","ErrorDetails":"...detailed error message (if any)"}]
     * OCRExitCode : 2
     * IsErroredOnProcessing : false
     * ErrorMessage : null
     * ErrorDetails : null
     * ProcessingTimeInMilliseconds : 3000
     */

    private String OCRExitCode;
    private boolean IsErroredOnProcessing;
    private Object ErrorMessage;
    private Object ErrorDetails;
    private String ProcessingTimeInMilliseconds;
    /**
     * TextOverlay : {"Lines":[{"Words":[{"WordText":"Word 1","Left":106,"Top":91,"Height":9,"Width":11},{"WordText":"Word 2","Left":121,"Top":90,"Height":13,"Width":51}],"MaxHeight":13,"MinTop":90}],"HasOverlay":true,"Message":null}
     * FileParseExitCode : 1
     * ParsedText : This is a sample parsed result
     * ErrorMessage : null
     * ErrorDetails : null
     */

    private List<ParsedResultsBean> ParsedResults;

    public String getOCRExitCode() {
        return OCRExitCode;
    }

    public void setOCRExitCode(String OCRExitCode) {
        this.OCRExitCode = OCRExitCode;
    }

    public boolean isIsErroredOnProcessing() {
        return IsErroredOnProcessing;
    }

    public void setIsErroredOnProcessing(boolean IsErroredOnProcessing) {
        this.IsErroredOnProcessing = IsErroredOnProcessing;
    }

    public Object getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(Object ErrorMessage) {
        this.ErrorMessage = ErrorMessage;
    }

    public Object getErrorDetails() {
        return ErrorDetails;
    }

    public void setErrorDetails(Object ErrorDetails) {
        this.ErrorDetails = ErrorDetails;
    }

    public String getProcessingTimeInMilliseconds() {
        return ProcessingTimeInMilliseconds;
    }

    public void setProcessingTimeInMilliseconds(String ProcessingTimeInMilliseconds) {
        this.ProcessingTimeInMilliseconds = ProcessingTimeInMilliseconds;
    }

    public List<ParsedResultsBean> getParsedResults() {
        return ParsedResults;
    }

    public void setParsedResults(List<ParsedResultsBean> ParsedResults) {
        this.ParsedResults = ParsedResults;
    }

    public static class ParsedResultsBean {
        /**
         * Lines : [{"Words":[{"WordText":"Word 1","Left":106,"Top":91,"Height":9,"Width":11},{"WordText":"Word 2","Left":121,"Top":90,"Height":13,"Width":51}],"MaxHeight":13,"MinTop":90}]
         * HasOverlay : true
         * Message : null
         */

        private TextOverlayBean TextOverlay;
        private String FileParseExitCode;
        private String ParsedText;
        private Object ErrorMessage;
        private Object ErrorDetails;

        public TextOverlayBean getTextOverlay() {
            return TextOverlay;
        }

        public void setTextOverlay(TextOverlayBean TextOverlay) {
            this.TextOverlay = TextOverlay;
        }

        public String getFileParseExitCode() {
            return FileParseExitCode;
        }

        public void setFileParseExitCode(String FileParseExitCode) {
            this.FileParseExitCode = FileParseExitCode;
        }

        public String getParsedText() {
            return ParsedText;
        }

        public void setParsedText(String ParsedText) {
            this.ParsedText = ParsedText;
        }

        public Object getErrorMessage() {
            return ErrorMessage;
        }

        public void setErrorMessage(Object ErrorMessage) {
            this.ErrorMessage = ErrorMessage;
        }

        public Object getErrorDetails() {
            return ErrorDetails;
        }

        public void setErrorDetails(Object ErrorDetails) {
            this.ErrorDetails = ErrorDetails;
        }

        public static class TextOverlayBean {
            private boolean HasOverlay;
            private Object Message;
            /**
             * Words : [{"WordText":"Word 1","Left":106,"Top":91,"Height":9,"Width":11},{"WordText":"Word 2","Left":121,"Top":90,"Height":13,"Width":51}]
             * MaxHeight : 13
             * MinTop : 90
             */

            private List<LinesBean> Lines;

            public boolean isHasOverlay() {
                return HasOverlay;
            }

            public void setHasOverlay(boolean HasOverlay) {
                this.HasOverlay = HasOverlay;
            }

            public Object getMessage() {
                return Message;
            }

            public void setMessage(Object Message) {
                this.Message = Message;
            }

            public List<LinesBean> getLines() {
                return Lines;
            }

            public void setLines(List<LinesBean> Lines) {
                this.Lines = Lines;
            }

            public static class LinesBean {
                private int MaxHeight;
                private int MinTop;
                /**
                 * WordText : Word 1
                 * Left : 106
                 * Top : 91
                 * Height : 9
                 * Width : 11
                 */

                private List<WordsBean> Words;

                public int getMaxHeight() {
                    return MaxHeight;
                }

                public void setMaxHeight(int MaxHeight) {
                    this.MaxHeight = MaxHeight;
                }

                public int getMinTop() {
                    return MinTop;
                }

                public void setMinTop(int MinTop) {
                    this.MinTop = MinTop;
                }

                public List<WordsBean> getWords() {
                    return Words;
                }

                public void setWords(List<WordsBean> Words) {
                    this.Words = Words;
                }

                public static class WordsBean {
                    private String WordText;
                    private int Left;
                    private int Top;
                    private int Height;
                    private int Width;

                    public String getWordText() {
                        return WordText;
                    }

                    public void setWordText(String WordText) {
                        this.WordText = WordText;
                    }

                    public int getLeft() {
                        return Left;
                    }

                    public void setLeft(int Left) {
                        this.Left = Left;
                    }

                    public int getTop() {
                        return Top;
                    }

                    public void setTop(int Top) {
                        this.Top = Top;
                    }

                    public int getHeight() {
                        return Height;
                    }

                    public void setHeight(int Height) {
                        this.Height = Height;
                    }

                    public int getWidth() {
                        return Width;
                    }

                    public void setWidth(int Width) {
                        this.Width = Width;
                    }
                }
            }
        }
    }
}
