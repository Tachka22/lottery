package org.lottery.service;

import com.google.inject.Inject;
import org.lottery.dto.request.LotteryTypeCreateRequest;
import org.lottery.model.LotteryType;
import org.lottery.repository.LotteryTypeRepository;

import java.util.List;
import java.util.regex.Pattern;

public class LotteryTypeServiceImpl implements LotteryTypeService {
    private final LotteryTypeRepository repository;

    // В имени должны быть только заглавные латинские буквы или подчеркивания, длина от 3 до 30 символов
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Z_]{3,30}$");

    @Inject
    public LotteryTypeServiceImpl(LotteryTypeRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<LotteryType> getAllTypes() {
        return repository.findAll();
    }

    @Override
    public LotteryType createType(LotteryTypeCreateRequest req) {
        validate(req);

        LotteryType type = new LotteryType();
        type.setName(req.getName().trim());
        type.setNumbersCount(req.getNumbersCount());
        type.setMinNumber(req.getMinNumber());
        type.setMaxNumber(req.getMaxNumber());
        type.setHasBonus(Boolean.TRUE.equals(req.getHasBonus()));
        type.setBonusMin(req.getBonusMin());
        type.setBonusMax(req.getBonusMax());
        type.setDescription(req.getDescription() != null ? req.getDescription().trim() : null);

        return repository.save(type);
    }

    private void validate(LotteryTypeCreateRequest req) {
        // Некорректное имя
        if (req.getName() == null || !NAME_PATTERN.matcher(req.getName()).matches()) {
            throw new IllegalArgumentException("name должно соответствовать ^[A-Z_]{3,30}$");
        }

        // Такой тип уже есть
        if (repository.existsByName(req.getName())) {
            throw new IllegalArgumentException("тип лотереи '" + req.getName() + "' уже существует");
        }

        // Некорректное количество чисел
        if (req.getNumbersCount() == null || req.getNumbersCount() < 1) {
            throw new IllegalArgumentException("numbersCount должен быть >= 1");
        }

        // Некорректный диапазон
        if (req.getMinNumber() == null || req.getMinNumber() < 1) {
            throw new IllegalArgumentException("minNumber должен быть >= 1");
        }
        if (req.getMaxNumber() == null || req.getMaxNumber() < 1) {
            throw new IllegalArgumentException("maxNumber должен быть >= 1");
        }
        if (req.getMinNumber() >= req.getMaxNumber()) {
            throw new IllegalArgumentException("minNumber должен быть строго меньше maxNumber");
        }

        // Диапазон должен вмещать количество уникальных чисел
        int availablePool = req.getMaxNumber() - req.getMinNumber() + 1;
        if (req.getNumbersCount() > availablePool) {
            throw new IllegalArgumentException(String.format(
                    "запрошенное количество numbersCount (%d) не помещается в диапазон от %d до %d; доступно %d чисел",
                    req.getNumbersCount(), req.getMinNumber(), req.getMaxNumber(), availablePool
            ));
        }

        // Количество чисел для выбора должно быть не больше 50% от диапазона
        if (req.getNumbersCount() > 0.5 * availablePool) {
            throw new IllegalArgumentException(
                    String.format("количество чисел для выбора (numbersCount) должно быть" +
                    " не больше 50%% от диапазона, при диапазоне от %d до %d максимум %d для выбора",
                    req.getMinNumber(), req.getMaxNumber(), (int) (0.5 * availablePool)));
        }

        // Некорректный диапазон бонусных чисел
        if (Boolean.TRUE.equals(req.getHasBonus())) {
            if (req.getBonusMin() == null || req.getBonusMax() == null) {
                throw new IllegalArgumentException("hasBonus = true требует обязательных bonusMin и bonusMax");
            }
            if (req.getBonusMin() < 1 || req.getBonusMax() < 1) {
                throw new IllegalArgumentException("bonusMin и bonusMax должны быть >= 1");
            }
            if (req.getBonusMin() >= req.getBonusMax()) {
                throw new IllegalArgumentException("bonusMin должен быть строго меньше bonusMax");
            }
            // Пересечение диапазонов min-max и bonusMin-bonusMax разрешено.
        }
    }
}