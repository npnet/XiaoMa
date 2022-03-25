package com.xiaoma.assistant.scenarios;

import android.content.Context;

import com.xiaoma.assistant.manager.IAssistantManager;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.utils.Constants;

import java.util.HashMap;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/31
 * Desc：场景分发器
 */
public class ScenarioDispatcher {
    protected IAssistantManager assistantManager;
    private static HashMap<String, IatScenario> iatScenarios = new HashMap<>();

    private static ScenarioDispatcher instance;

    private ScenarioDispatcher() {

    }

    public static ScenarioDispatcher getInstance() {
        if (instance == null) {
            instance = new ScenarioDispatcher();
        }
        return instance;
    }

    public void init(IAssistantManager assistantManager) {
        this.assistantManager = assistantManager;
    }

    public IatScenario dispatch(Context context, LxParseResult parserResult) {
        if (parserResult == null) {
            return new IatErrorScenario(context);
        }
        if (parserResult.getRc() == 4) {
            return new IatErrorScenario(context);
        }
        IatScenario scenario = null;
        if (parserResult.isMapAction()) {
            //定位
            scenario = getIatLocationScenario(context);
        } else if (parserResult.isRoutAction()) {
            //导航
            scenario = getIatNaviScenario(context);
        } else if (parserResult.isMusicAction()) {
            //音乐
            if (iatScenarios.get(Constants.ScenarioFactoryType.MUSIC) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.MUSIC, new IatMusicScenario(context));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.MUSIC);
        } else if (parserResult.isWeatherAction()) {
            //天气
            scenario = getIatWeatherScenario(context);
        } else if (parserResult.isFlightAction()) {
            //航班
            if (iatScenarios.get(Constants.ScenarioFactoryType.FLIGHT) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.FLIGHT, new IatFlightScenario(context));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.FLIGHT);
        } else if (parserResult.isConstellationAction()) {
            //星座
            if (iatScenarios.get(Constants.ScenarioFactoryType.CONSTELLATION) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.CONSTELLATION, new IatConstellationScenario(context));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.CONSTELLATION);
        } else if (parserResult.isFmAction()) {
            //fm调频/am调幅
            if (iatScenarios.get(Constants.ScenarioFactoryType.FM) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.FM, new IatFmScenario(context));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.FM);
        } else if (parserResult.isRadioAction()) {
            //网络电台
            if (iatScenarios.get(Constants.ScenarioFactoryType.RADIO) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.RADIO, new IatRadioScenario(context));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.RADIO);
        } else if (parserResult.isProgramAction()) {
            //在线节目
            scenario = getIatProgramScenario(context);
        } else if (parserResult.isPhoneCallAction()) {
            //拨打电话
            scenario = getIatCallScenario(context);
        } else if (parserResult.isCarFaultAction()) {
            //故障表
            if (iatScenarios.get(Constants.ScenarioFactoryType.CARFAULT) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.CARFAULT, new IatCarFaultScenario(context));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.CARFAULT);
        } else if (parserResult.isTranslateAction()) {
            //翻译
            if (iatScenarios.get(Constants.ScenarioFactoryType.TRANSLATION) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.TRANSLATION, new IatTranslationScenario(context));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.TRANSLATION);
        } else if (parserResult.isGasOnlineAction()) {
            //查询油价
            if (iatScenarios.get(Constants.ScenarioFactoryType.GAS) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.GAS, new IatGasScenario(context));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.GAS);
        } else if (parserResult.isStockAction()) {
            //股票
            if (iatScenarios.get(Constants.ScenarioFactoryType.STOCK) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.STOCK, new IatStockScenario(context));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.STOCK);
        } else if (parserResult.isTrainQueryAction()) {
            //火车
            if (iatScenarios.get(Constants.ScenarioFactoryType.TRAIN) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.TRAIN, new IatTrainScenario(context));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.TRAIN);
        } else if (parserResult.isWebsiteAction()) {
            //网页
            if (iatScenarios.get(Constants.ScenarioFactoryType.WEB) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.WEB, new IatWebsiteScenario(context));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.WEB);
        } else if (parserResult.isWebSearchAction()) {
            //网页查询
            if (iatScenarios.get(Constants.ScenarioFactoryType.WEBSEARCH) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.WEBSEARCH, new IatWebSearchScenario(context));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.WEBSEARCH);
        } else if (parserResult.isDateTimeQueryAction()) {
            //查询日期
            if (iatScenarios.get(Constants.ScenarioFactoryType.TIME) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.TIME, new IatDateScenario(context));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.TIME);
        } else if (parserResult.isQueryViolationAction()) {
            //违章查询
            if (iatScenarios.get(Constants.ScenarioFactoryType.VIOLATION) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.VIOLATION, new IatQueryViolationScenario(context));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.VIOLATION);
        } else if (parserResult.isTrafficAction()) {
            //路况
            if (iatScenarios.get(Constants.ScenarioFactoryType.TRAFFIC) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.TRAFFIC, new IatTrafficScenario(context));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.TRAFFIC);
        } else if (parserResult.isNearbyCarAction()) {
            //搜附近的车
            if (iatScenarios.get(Constants.ScenarioFactoryType.CAR) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.CAR, new IatNearbyCarScenario(context));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.CAR);
        }
        /*else if (parserResult.isParkSearchAction()) {
            //搜附近的停车场
            if (iatScenarios.get(Constants.ScenarioFactoryType.PARKING) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.PARKING, new IatParkScenario(context));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.PARKING);
        }*/
        else if (parserResult.isLimitAction()) {
            //查询限行
            if (iatScenarios.get(Constants.ScenarioFactoryType.LIMIT) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.LIMIT, new IatLimitScenario(context));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.LIMIT);
        } else if (parserResult.isCmdAciton() && parserResult.isInstructionAction()) {
            //指令
            scenario = getIatInstructionScenario(context);
        } else if (parserResult.isAirControlAction()) {
            //空调控制
            if (iatScenarios.get(Constants.ScenarioFactoryType.AIR) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.AIR, new AirControlScenario(context, parserResult));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.AIR);
        } else if (parserResult.isCmdTimeAction()) {
            //车载解析时间
            if (iatScenarios.get(Constants.ScenarioFactoryType.TIME) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.TIME, new IatDateScenario(context));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.TIME);
        } else if (parserResult.isChangeWakeupAction()) {
            //唤醒词修改
            if (iatScenarios.get(Constants.ScenarioFactoryType.WAKEUP) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.WAKEUP, new IatWakeUpScenario(context));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.WAKEUP);
        } else if (parserResult.isTextAnswerAction()) {
            //问答
            if (iatScenarios.get(Constants.ScenarioFactoryType.ANSWER) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.ANSWER, new IatAnswerScenario(context));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.ANSWER);
        } else if (parserResult.isScheduleAction()) {
            //日程
            if (iatScenarios.get(Constants.ScenarioFactoryType.SCHEDULE) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.SCHEDULE, new IatScheduleScenario(context));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.SCHEDULE);
        } else if (parserResult.isImageSearchAction()) {
            // 搜索图片
            if (iatScenarios.get(Constants.ScenarioFactoryType.IMAGE) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.IMAGE, new IatImageScenario(context));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.IMAGE);
        } else if (parserResult.isImageQueryAction()) {
            // 搜索图片
            if (iatScenarios.get(Constants.ScenarioFactoryType.IMAGE) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.IMAGE, new IatImageScenario(context));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.IMAGE);
        } else if (parserResult.isNewsAction()) {
            // 搜索新闻
            scenario = getIatNewsScenario(context);
        } else if (parserResult.isTravelSearchAction()) {
            //搜景点
            if (iatScenarios.get(Constants.ScenarioFactoryType.TRAVEL) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.TRAVEL, new IatTravelScenario(context));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.TRAVEL);
        }
       /* else if (parserResult.isRestaurantSearchAction()) {
            //搜美食
            if (iatScenarios.get(Constants.ScenarioFactoryType.RESTAURANT) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.RESTAURANT, new IatRestaurantScenario(context));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.RESTAURANT);
        }*/
        else if (parserResult.isVehicleAction()) {//
            //车况查询
            if (iatScenarios.get(Constants.ScenarioFactoryType.VEHICLE) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.VEHICLE, new IatVehicleScenario(context));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.VEHICLE);
        } else if (parserResult.isCarControlSetAction()) {
            //设备控制
            if (iatScenarios.get(Constants.ScenarioFactoryType.CAR_CONTROL) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.CAR_CONTROL, new IatCarControlScenario(context));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.CAR_CONTROL);
        } else if (parserResult.isAppControl()) {
            //app控制
            if (iatScenarios.get(Constants.ScenarioFactoryType.APP_CONTROL) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.APP_CONTROL, new IatAppControlScenario(context));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.APP_CONTROL);
        } else if (parserResult.isMessageAciton()) {
            //车载微信
            scenario = IatWeChatScenario(context);
        } else if (parserResult.isHistoryToday()) {
            //历史今天
            if (iatScenarios.get(Constants.ScenarioFactoryType.HISTORY_TODAY) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.HISTORY_TODAY, new IatHistoryTodayScenario(context));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.HISTORY_TODAY);
        } else if (parserResult.isVideo()) {
            //视频
            if (iatScenarios.get(Constants.ScenarioFactoryType.VIDEO) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.VIDEO, new IatVideoScenario(context));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.VIDEO);
        } else if (parserResult.isSmartHome()) {
            //智能家居
            if (iatScenarios.get(Constants.ScenarioFactoryType.SMARTHOME) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.SMARTHOME, new IatSmartHomeScenario(context));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.SMARTHOME);
        }else if (parserResult.isAllSmartHome()) {
            //小米智能家居
            if (iatScenarios.get(Constants.ScenarioFactoryType.ALLSMARTHOME) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.ALLSMARTHOME, new IatAllSmartHomeScenario(context));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.ALLSMARTHOME);
        } else if (parserResult.isFlow()) {
            //流量相关
            scenario = IatFlowScenario(context);
        } else if (parserResult.isHelpAciton()) {
            if (iatScenarios.get(Constants.ScenarioFactoryType.HELPER) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.HELPER, new IatHelperScenario(context));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.HELPER);
        } else {
            if (iatScenarios.get(Constants.ScenarioFactoryType.ERROR) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.ERROR, new IatErrorScenario(context));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.ERROR);
        }

        //预防对象为空
        if (scenario == null) {
            if (iatScenarios.get(Constants.ScenarioFactoryType.ERROR) == null) {
                iatScenarios.put(Constants.ScenarioFactoryType.ERROR, new IatErrorScenario(context));
            }
            scenario = iatScenarios.get(Constants.ScenarioFactoryType.ERROR);
        }

        return scenario;
    }

    public IatRestaurantScenario getIatRestaurantScenario(Context context) {
        IatRestaurantScenario iatScenario = (IatRestaurantScenario) iatScenarios.get(Constants.ScenarioFactoryType.RESTAURANT);
        if (iatScenario == null) {
            iatScenario = new IatRestaurantScenario(context);
            iatScenarios.put(Constants.ScenarioFactoryType.RESTAURANT, iatScenario);
        }
        return iatScenario;
    }

    public IatParkScenario getIatParkScenario(Context context) {
        IatParkScenario iatScenario = (IatParkScenario) iatScenarios.get(Constants.ScenarioFactoryType.PARKING);
        if (iatScenario == null) {
            iatScenario = new IatParkScenario(context);
            iatScenarios.put(Constants.ScenarioFactoryType.PARKING, iatScenario);
        }
        return iatScenario;
    }

    public IatCinemaScenario getIatCinemaScenario(Context context) {
        IatCinemaScenario iatScenario = (IatCinemaScenario) iatScenarios.get(Constants.ScenarioFactoryType.CINEMA);
        if (iatScenario == null) {
            iatScenario = new IatCinemaScenario(context);
            iatScenarios.put(Constants.ScenarioFactoryType.CINEMA, iatScenario);
        }
        return iatScenario;
    }

    public IatHotelScenario getIatHotelScenario(Context context) {
        IatHotelScenario iatScenario = (IatHotelScenario) iatScenarios.get(Constants.ScenarioFactoryType.HOTEL);
        if (iatScenario == null) {
            iatScenario = new IatHotelScenario(context);
            iatScenarios.put(Constants.ScenarioFactoryType.HOTEL, iatScenario);
        }
        return iatScenario;
    }

    public IatProgramScenario getIatProgramScenario(Context context) {
        IatProgramScenario iatScenario = (IatProgramScenario) iatScenarios.get(Constants.ScenarioFactoryType.PROGRAM);
        if (iatScenario == null) {
            iatScenario = new IatProgramScenario(context);
            iatScenarios.put(Constants.ScenarioFactoryType.PROGRAM, iatScenario);
        }
        return iatScenario;
    }

    public IatImageScenario getIatImageScenario(Context context) {
        IatImageScenario iatScenario = (IatImageScenario) iatScenarios.get(Constants.ScenarioFactoryType.IMAGE);
        if (iatScenario == null) {
            iatScenario = new IatImageScenario(context);
            iatScenarios.put(Constants.ScenarioFactoryType.IMAGE, iatScenario);
        }
        return iatScenario;
    }

    public IatInstructionScenario getIatInstructionScenario(Context context) {
        IatInstructionScenario iatScenario = (IatInstructionScenario) iatScenarios.get(Constants.ScenarioFactoryType.INSTRUCTION);
        if (iatScenario == null) {
            iatScenario = new IatInstructionScenario(context);
            iatScenarios.put(Constants.ScenarioFactoryType.INSTRUCTION, iatScenario);
        }
        return iatScenario;
    }

    public IatCallScenario getIatCallScenario(Context context) {
        IatCallScenario iatScenario = (IatCallScenario) iatScenarios.get(Constants.ScenarioFactoryType.PHONE);
        if (iatScenario == null) {
            iatScenario = new IatCallScenario(context);
            iatScenarios.put(Constants.ScenarioFactoryType.PHONE, iatScenario);
        }
        return iatScenario;
    }

    public IatWeatherScenario getIatWeatherScenario(Context context) {
        IatWeatherScenario iatScenario = (IatWeatherScenario) iatScenarios.get(Constants.ScenarioFactoryType.WEATHER);
        if (iatScenario == null) {
            iatScenario = new IatWeatherScenario(context);
            iatScenarios.put(Constants.ScenarioFactoryType.WEATHER, iatScenario);
        }
        return iatScenario;
    }

    public IatNewsScenario getIatNewsScenario(Context context) {
        IatNewsScenario iatScenario = (IatNewsScenario) iatScenarios.get(Constants.ScenarioFactoryType.NEWS);
        if (iatScenario == null) {
            iatScenario = new IatNewsScenario(context);
            iatScenarios.put(Constants.ScenarioFactoryType.NEWS, iatScenario);
        }
        return iatScenario;
    }

    public IatLocationScenario getIatLocationScenario(Context context) {
        IatLocationScenario iatScenario = (IatLocationScenario) iatScenarios.get(Constants.ScenarioFactoryType.LOCATION);
        if (iatScenario == null) {
            iatScenario = new IatLocationScenario(context);
            iatScenarios.put(Constants.ScenarioFactoryType.LOCATION, iatScenario);
        }
        return iatScenario;
    }

    public IatNaviScenario getIatNaviScenario(Context context) {
        IatNaviScenario iatScenario = (IatNaviScenario) iatScenarios.get(Constants.ScenarioFactoryType.NAVI);
        if (iatScenario == null) {
            iatScenario = new IatNaviScenario(context);
            iatScenarios.put(Constants.ScenarioFactoryType.NAVI, iatScenario);
        }
        return iatScenario;
    }

    public IatWeChatScenario IatWeChatScenario(Context context) {
        IatWeChatScenario iatScenario = (IatWeChatScenario) iatScenarios.get(Constants.ScenarioFactoryType.MESSAGE);
        if (iatScenario == null) {
            iatScenario = new IatWeChatScenario(context);
            iatScenarios.put(Constants.ScenarioFactoryType.MESSAGE, iatScenario);
        }
        return iatScenario;
    }

    public IatFlowScenario IatFlowScenario(Context context) {
        IatFlowScenario iatScenario = (IatFlowScenario) iatScenarios.get(Constants.ScenarioFactoryType.FLOW);
        if (iatScenario == null) {
            iatScenario = new IatFlowScenario(context);
            iatScenarios.put(Constants.ScenarioFactoryType.FLOW, iatScenario);
        }
        return iatScenario;
    }
    
}
