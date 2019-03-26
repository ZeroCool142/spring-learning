-- Показать всех должников (чей товар доставлен, а сумма по договору не совпадает с суммой по платежам) по договорам с сортировкой по максимальному долгу
--explain analyze
select name, sum(contract.amount - summa) as debt
from learnspring.contract
	join learnspring.organization using (organization_id)
	join (
		select sum(amount) as summa, contract_id
		from learnspring.payment
		group by contract_id
	) pay using (contract_id)
where pay.summa != contract.amount
	and contract.contract_id not in (
		select contract_id
		from learnspring.shipment
		where actual_date_arrival is null
	)
group by name
order by debt desc

-- Показать самых лучших клиентов (принесших большую прибыль)
--explain analyze
select organization.name, sum(payment.amount) as payed
from learnspring.payment
	join learnspring.contract using (contract_id)
	join learnspring.organization using (organization_id)
group by name
order by payed desc limit 10

-- Показать самый популярный продукт доставки и количество за месяц
--explain analyze
select product.product_id, product.name, sum(amount) as quantity
from learnspring.shipment
	join learnspring.shipment_product using (shipment_id)
	join learnspring.product using (product_id)
where date_part('month', shipment.expected_date_shipment) = date_part('month', current_date)
group by product.product_id, product.name
order by quantity desc limit 1

-- Показать самый популярный город доставки
--explain analyze
select name
from learnspring.shipment
	join learnspring."location" using (location_id)
	join learnspring.city using (city_id)
group by name
order by count(city_id) desc limit 1

-- Показать информацию по самым долгим, в плане доставки, направлениям
--explain analyze
select name, address, max(shipment_interval(actual_date_shipment, actual_date_arrival)) as days_travel
from learnspring.shipment
	join learnspring."location" using (location_id)
	join learnspring.city using (city_id)
where actual_date_shipment is not null
	and actual_date_arrival is not null
group by name, address
order by days_travel desc limit 5

create or replace function shipment_interval(s date, a date) returns integer as $$
declare
	e_year integer := cast(date_part('year', $2) as integer) - cast(date_part('year', $1) as integer);
	e_month integer := cast(date_part('month', $2) as integer) - cast(date_part('month', $1) as integer);
	e_day integer := cast(date_part('day', $2) as integer) - cast(date_part('day', $1) as integer);
begin
	return extract('epoch' from make_interval(e_year, e_month, 0, e_day))/86400;
end
$$
LANGUAGE plpgsql;