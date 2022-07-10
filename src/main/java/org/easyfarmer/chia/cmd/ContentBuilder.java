package org.easyfarmer.chia.cmd;


import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liyifeng
 * @version 1.0
 * @date 2021/6/23 7:07 下午
 */
public class ContentBuilder {
    //private static final Logger log = LoggerFactory.getLogger(ContentBuilder.class);

    private static boolean isInt(Field f) {
        return f.getType().getTypeName().contains("int") || f.getType().getTypeName().contains("java.lang.Integer");
    }


    private static boolean isLong(Field f) {
        return f.getType().getTypeName().contains("long") || f.getType().getTypeName().contains("java.lang.Long");
    }

    private static boolean isDouble(Field f) {
        return f.getType().getTypeName().contains("double") || f.getType().getTypeName().contains("java.lang.Double");
    }

    private static boolean isString(Field f) {
        return f.getType().getTypeName().contains("java.lang.String");
    }

    private static boolean isBoolean(Field f) {
        return f.getType().getTypeName().contains("boolean") || f.getType().getTypeName().contains("java.lang.Boolean");
    }

    private static boolean isPlotSize(Field f) {
        return f.getType().getTypeName().contains("cn.c4dig.chia.manage.bean.PlotSize");
    }

    public static Map<String, String> listResult2Map(List<String> lines){
        return listResult2Map(lines,":");
    }

    public static <T extends ChiaForkResult> T setValue(T obj, List<String> lines) {
        Map<String, String> valueMap = listResult2Map(lines);
        Map<String, String> fieldMap = obj.getFieldMap();
        if (fieldMap == null || fieldMap.size() == 0) {
            obj.resolveLines(lines);
            return obj;
        }
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field f : fields) {
            String fieldName = fieldMap.get(f.getName());
            if (StrUtil.isNotBlank(fieldName)) {
                try {
                    f.setAccessible(true);

                    if (isString(f)) {
                        f.set(obj, getStringValueFromMap(valueMap, fieldName));
                    } else if (isInt(f)) {
                        Integer intValueFromMap = getIntValueFromMap(valueMap, fieldName);
                        if (intValueFromMap != null) {
                            f.set(obj, new Integer(intValueFromMap));
                        }
                    } else if (isLong(f)) {
                        Long longValueFromMap = getLongValueFromMap(valueMap, fieldName);
                        if (longValueFromMap != null) {
                            f.set(obj, longValueFromMap);
                        }
                    } else if (isBoolean(f)) {
                        Boolean booleanValueFromMap = getBooleanValueFromMap(valueMap, fieldName);
                        if (booleanValueFromMap != null) {
                            f.setBoolean(obj, new Boolean(booleanValueFromMap));
                        }
                    } else if (isDouble(f)) {
                        Double doubleValueFromMap = getDoubleValueFromMap(valueMap, fieldName);
                        if (doubleValueFromMap != null) {
                            f.set(obj, new Double(doubleValueFromMap));
                        }
                    } /*else if (isFarmingStatus(f)) {
                            ChiaFarmSummary.FARMING_STATUS status = ChiaFarmSummary.FARMING_STATUS.from(getStringValueFromMap(valueMap, fieldName));
                            if (status != null) {
                                f.set(res, status.getDesc());
                            } else {
                                f.set(res, getStringValueFromMap(valueMap, fieldName));
                            }
                        }*/ else {
                        f.set(obj, valueMap.get(fieldName));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        obj.resolveLines(lines);
        return obj;
    }
    /**
     *  把逐行字符串按照分隔符转换成map
     * @param lines
     * @param delimiter
     * @return
     */
    public static Map<String, String> listResult2Map(List<String> lines,String delimiter) {
        Map<String, String> map = new HashMap<>();
        if (lines == null || lines.size() == 0) {
            return map;
        }


        for (String line : lines) {
            String[] kvArr = line.split(":");
            if (kvArr.length == 2) {
                map.put(kvArr[0].trim(), kvArr[1] == null ? "" : kvArr[1].trim());
            } else if (kvArr.length == 1) {
                map.put(kvArr[0].trim(), "");
            }
        }

        return map;
    }

    private static Boolean getBooleanValueFromMap(Map<String, String> valueMap, String fieldName) {
        String v = getStringValueFromMap(valueMap, fieldName);
        if (v != null) {
            return false;
        }
        return BooleanUtil.toBoolean(v);
    }

    private static Long getLongValueFromMap(Map<String, String> valueMap, String fieldName) {
        String v = getStringValueFromMap(valueMap, fieldName);
        if (v != null) {
            try {
                return Long.valueOf(v);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    private static Integer getIntValueFromMap(Map<String, String> valueMap, String fieldName) {
        String v = getStringValueFromMap(valueMap, fieldName);
        if (v != null) {
            try {
                return Integer.valueOf(v);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    private static String getStringValueFromMap(Map<String, String> valueMap, String fieldName) {
        return valueMap.get(fieldName);
    }

    private static Double getDoubleValueFromMap(Map<String, String> valueMap, String fieldName) {
        String v = getStringValueFromMap(valueMap, fieldName);
        if (v != null) {
            try {
                return Double.valueOf(v);
            } catch (Exception e) {
            }
        }
        return null;
    }
}
